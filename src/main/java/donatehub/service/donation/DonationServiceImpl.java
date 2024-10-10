package donatehub.service.donation;

import donatehub.domain.embeddables.DonationPayment;
import donatehub.domain.projections.DonationStatistic;
import donatehub.domain.response.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import donatehub.domain.entities.DonationEntity;
import donatehub.domain.entities.UserEntity;
import donatehub.domain.entities.WidgetEntity;
import donatehub.domain.exceptions.BaseException;
import donatehub.domain.projections.DonationInfo;
import donatehub.domain.request.DonationCreateRequest;
import donatehub.repo.DonationRepository;
import donatehub.service.payment.ClickService;
import donatehub.utils.BotExecutor;
import donatehub.service.user.UserService;
import donatehub.service.widget.WidgetService;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final Logger log;
    private final DonationRepository repo;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WidgetService widgetService;
    private final ClickService clickService;
    private final BotExecutor botExecutor;
    private final RedisTemplate<String, DonationEntity> redisTemplate;

    @Override
    public CreateDonateResponse donate(DonationCreateRequest donateReq, Long streamerId) {
        log.info("Donat qabul qilinmoqda. Streamer ID: {}, Donat miqdori: {}", streamerId, donateReq.getAmount());

        if (!userService.findById(streamerId).getEnable()) {
            throw new BaseException(
                    "Streamer tasdiqlanmagan",
                    HttpStatus.BAD_REQUEST
            );
        }

        DonationPayment payment = DonationPayment.builder()
                .amount(donateReq.getAmount())
                .commission(getCommission(donateReq))
                .build();

        PaymentResponse paymentResponse =
                clickService.create(donateReq.getAmount() + payment.getCommission());

        payment.setPaymentId(paymentResponse.getId());


        DonationEntity donation = DonationEntity.builder()
                .donaterName(donateReq.getDonaterName())
                .message(donateReq.getMessage())
                .streamer(userService.findById(streamerId))
                .payment(payment)
                .build();

        log.info("Donat Redisda saqlanmoqda: {}", payment.getPaymentId());
        redisTemplate.opsForValue().set("donation:" + paymentResponse.getId(), donation, 20, TimeUnit.MINUTES);

        return new CreateDonateResponse(paymentResponse.getRedirectUrl());
    }

    @Override
    public void complete(DonationEntity donation) {
        log.info("Donat to'liq amalga oshirildi: {}", donation.getId());

        repo.save(donation);

        log.info("Streamer balansini qayta hisoblanmoqda: {}", donation.getStreamer().getId());

        userService.recalculateStreamerBalance(donation.getStreamer().getId(), donation.getPayment().getAmount());

        if (userService.findById(donation.getStreamer().getId()).getOnline()) {
            log.info("Streamer onlayn, donatni jonli efirga uzatilmoqda: {}", donation.getStreamer().getId());

            executeToStream(donation);
        }

        log.info("Telegram botga xabar yuborilyapti: {}", donation.getStreamer().getId());
        botExecutor.sendDonationInfo(donation.getStreamer().getId(), donation);
    }

    private void executeToStream(DonationEntity donation) {
        log.info("Donatni jonli efirga uzatish: {}", donation);

        WidgetEntity widget = widgetService.getWidgetOfStreamer(donation.getStreamer().getId(), donation.getPayment().getAmount());
        messagingTemplate.convertAndSend("/topic/donation/" + donation.getStreamer().getToken(),
                new DonationResponse(donation.getDonaterName(), donation.getMessage(),
                        donation.getPayment().getAmount(), widget.getVideoUrl(), widget.getAudioUrl()));
    }

    private Float getCommission(DonationCreateRequest donateReq) {
        log.info("Komissiya hisoblanmoqda: miqdor {}", donateReq.getAmount());

        return donateReq.getAmount() / 100 * 5;
    }

    @Override
    public Page<DonationInfo> getDonationsOfStreamer(Long streamerId, int page, int size) {
        log.info("Streamer ID: {} uchun donatlar so'ralmoqda", streamerId);

        UserEntity user = userService.findById(streamerId);

        return repo.getAllByStreamerIdOrderByUpdateAt(user.getId(), PageRequest.of(page, size));
    }

    @Override
    public DonationStatistic getDonationStatistics() {
        return repo.getDonationStatistic();
    }

    @Override
    public DonationStatistic getDonationStatisticsOfStreamer(Long streamerId) {
        UserEntity user = userService.findById(streamerId);

        return repo.getDonationStatisticOfStreamer(user.getId());
    }

    @Override
    public void testDonate(DonationCreateRequest donationCreateRequest, UserEntity streamer) {
        log.info("Test donat amalga oshirilyapti: {}", streamer.getId());

        executeToStream(DonationEntity.builder()
                .donaterName(donationCreateRequest.getDonaterName())
                .message(donationCreateRequest.getMessage())
                .streamer(streamer)
                .payment(DonationPayment.builder()
                        .amount(donationCreateRequest.getAmount())
                        .commission(getCommission(donationCreateRequest))
                        .build())
                .build());
    }
}