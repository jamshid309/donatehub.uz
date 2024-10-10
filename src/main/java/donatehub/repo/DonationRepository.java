package donatehub.repo;

import donatehub.domain.projections.DonationStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import donatehub.domain.entities.DonationEntity;
import donatehub.domain.projections.DonationInfo;

import java.util.Optional;

public interface DonationRepository extends JpaRepository<DonationEntity, Long> {
    Page<DonationInfo> getAllByStreamerIdOrderByUpdateAt(Long streamer_id, Pageable pageable);

    @Query(
            value = """
            select
                count(dt.id) as totalCount,
                sum(dt.amount) as totalAmount,
                count(case when created_at >= CURRENT_DATE then 1 end) as dailyCount,
                coalesce(sum(case when created_at >= CURRENT_DATE then amount end), 0) as dailyAmount
            from donations_table dt
            """,
            nativeQuery = true
    )
    DonationStatistic getDonationStatistic();

    @Query(
            value = """
            select
                count(id) as totalCount,
                sum(amount) as totalAmount,
                count(case when created_at >= CURRENT_DATE then 1 end) as dailyCount,
                coalesce(sum(case when created_at >= CURRENT_DATE then amount end), 0) as dailyAmount
            from donations_table where streamer_id = :id
            """,
            nativeQuery = true
    )
    DonationStatistic getDonationStatisticOfStreamer(@Param("id") Long id);
}