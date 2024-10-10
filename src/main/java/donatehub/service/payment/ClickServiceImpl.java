package donatehub.service.payment;

import donatehub.domain.entities.DonationEntity;
import donatehub.domain.request.ClickCompleteRequest;
import donatehub.domain.request.ClickPrepareRequest;
import donatehub.domain.response.ClickCompleteResponse;
import donatehub.domain.response.ClickPrepareResponse;
import donatehub.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import donatehub.domain.exceptions.BaseException;
import donatehub.domain.response.PaymentResponse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClickServiceImpl implements ClickService {
    @Lazy
    @Autowired
    private DonationService donationService;

    private final RedisTemplate<String, DonationEntity> redisTemplate;

    @Value("${click.service.id}")
    private String serviceId;

    @Value("${click.merchant.id}")
    private String merchantId;

    @Value("${click.secret.key}")
    private String secretKey;

    @Value("${click.return.url}")
    private String returnUrl;

    @Override
    public PaymentResponse create(Float amount) {
        String id = UUID.randomUUID().toString();

        String url = String.format("https://my.click.uz/services/pay?service_id=%s&merchant_id=%s&amount=%s&transaction_param=%s&return_url=%s", serviceId, merchantId, amount, id, returnUrl);

        return new PaymentResponse(id, url);
    }

    @Override
    public ClickPrepareResponse prepare(ClickPrepareRequest clickRequest) {
        DonationEntity donation = redisTemplate.opsForValue().get("donation:" + clickRequest.getMerchantTransId());

        return checkPrepare(clickRequest, donation);
    }

    private ClickPrepareResponse checkPrepare(ClickPrepareRequest clickRequest, DonationEntity donation) {
        ClickPrepareResponse response = ClickPrepareResponse.builder()
                .clickTransId(clickRequest.getClickTransId())
                .merchantTransId(Integer.valueOf(clickRequest.getMerchantTransId()))
                .merchantPrepareId(Integer.valueOf(clickRequest.getMerchantTransId()))
                .error(0)
                .errorNote("")
                .build();

        if (clickRequest.getAction() != 0){
            response.setError(-3);
            return response;
        }

        if (donation == null) {
            response.setError(-6);
            return response;
        }

        String hash = md5(clickRequest.getClickTransId().toString(), serviceId, secretKey, clickRequest.getMerchantTransId(), donation.getPayment().getAmount(), 0, clickRequest.getSignTime());

        if (!hash.equals(clickRequest.getSignString())) {
            response.setError(-1);
            return response;
        }

        if (clickRequest.getAmount().equals(donation.getPayment().getAmount() + donation.getPayment().getCommission())) {
            response.setError(-2);
            return response;
        }

        if (response.getError() == 0) {
            donationService.complete(donation);
        }

        return response;
    }

    @Override
    public ClickCompleteResponse complete(ClickCompleteRequest clickRequest) {
        DonationEntity donation = redisTemplate.opsForValue().get("donation:" + clickRequest.getMerchantTransId());

        return checkComplete(clickRequest, donation);
    }

    private ClickCompleteResponse checkComplete(ClickCompleteRequest clickRequest, DonationEntity donation) {
        ClickCompleteResponse response = ClickCompleteResponse.builder()
                .clickTransId(clickRequest.getClickTransId())
                .serviceId(clickRequest.getServiceId())
                .merchantConfirmId(Integer.valueOf(clickRequest.getMerchantTransId()))
                .error(0)
                .errorNote("")
                .build();

        if (clickRequest.getAction() != 1) {
            response.setError(-3);
            return response;
        }

        if (donation == null) {
            response.setError(-6);
            return response;
        }

        String hash = md5(clickRequest.getClickTransId().toString(), serviceId, secretKey, clickRequest.getMerchantTransId(), donation.getPayment().getAmount(), 1, clickRequest.getSignTime());

        if (!hash.equals(clickRequest.getSignString())) {
            response.setError(-1);
            return response;
        }

        if (!clickRequest.getAmount().equals(donation.getPayment().getAmount() + donation.getPayment().getCommission())) {
            response.setError(-2);
            return response;
        }

        if (response.getError() == 0) {
            donationService.complete(donation);
        }

        return response;
    }

    private String md5(String clickTransId, String serviceId, String secretKey, String merchantTransId, Float amount, Integer action, String signTime) {
        String dataToHash = clickTransId + serviceId + secretKey + merchantTransId + amount + action + signTime;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(dataToHash.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BaseException(
                    "MD5 hash xatolik",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}