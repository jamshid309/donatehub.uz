package donatehub.service.donation;

import donatehub.domain.entities.DonationEntity;
import donatehub.domain.projections.DonationStatistic;
import org.springframework.data.domain.Page;
import donatehub.domain.entities.UserEntity;
import donatehub.domain.constants.PaymentMethod;
import donatehub.domain.response.CreateDonateResponse;
import donatehub.domain.projections.DonationInfo;
import donatehub.domain.request.DonationCreateRequest;

import java.util.List;

public interface DonationService {
    CreateDonateResponse donate(DonationCreateRequest donateReq, Long streamerId);

    Page<DonationInfo> getDonationsOfStreamer(Long streamerId, int page, int size);

    DonationStatistic getDonationStatistics();

    DonationStatistic getDonationStatisticsOfStreamer(Long streamerId);

    void complete(DonationEntity donation);

    void testDonate(DonationCreateRequest donationCreateRequest, UserEntity streamer);
}
