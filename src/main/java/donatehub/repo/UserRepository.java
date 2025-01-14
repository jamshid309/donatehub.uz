package donatehub.repo;

import donatehub.domain.projections.*;
import donatehub.domain.constants.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import donatehub.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserInfo> getByIdOrderByCreatedAt(Long id);

    Optional<UserInfoForDonate> findByChannelNameIgnoreCase(String username);

    Page<UserInfoForView> getAllByEnableAndRoleOrderByFullRegistered(Boolean approved, Pageable pageable, UserRole userRole);

    @Query("SELECT u FROM users_table u WHERE (LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND u.enable = :enable ORDER BY u.lastOnlineAt")
    Page<UserInfoForView> findAllByFirstNameOrUsernameAndEnable(@Param("email") String email, @Param("username") String username, @Param("enable") Boolean enable, Pageable pageable);

    @Query(
            value = "SELECT days AS date, " +
                    "COALESCE(COUNT(DISTINCT us.id), 0) AS onlineUsers, " +
                    "COALESCE(COUNT(DISTINCT us2.id), 0) AS registeredUsers, " +
                    "COALESCE(COUNT(DISTINCT dt.id), 0) AS donations, " +
                    "COALESCE(COUNT(DISTINCT wt.id), 0) AS withdraws " +
                    "FROM generate_series(current_date - INTERVAL '1 day' * :days, current_date, '1 day'::interval) AS days " +
                    "LEFT JOIN users_table us ON us.last_online_at::date = days " +
                    "LEFT JOIN donations_table dt ON dt.created_at::date = days " +
                    "LEFT JOIN withdraws_table wt ON wt.created_at::date = days " +
                    "LEFT JOIN users_table us2 ON us2.created_at::date = days " +
                    "GROUP BY days " +
                    "ORDER BY days",
            nativeQuery = true
    )
    List<AdminStatisticByGraphic> getStatistic(@Param("days") int days);

    @Query(
            value = "SELECT days AS day, " +
                    "COALESCE(COUNT(DISTINCT dt.id), 0) AS donations, " +
                    "COALESCE(COUNT(DISTINCT wt.id), 0) AS withdraws " +
                    "FROM generate_series(current_date - INTERVAL '1 day' * :days, current_date, '1 day'::interval) AS days " +
                    "LEFT JOIN donations_table dt ON dt.created_at::date = days AND dt.streamer_id = :id " +
                    "LEFT JOIN withdraws_table wt ON wt.created_at::date = days and wt.streamer_id = :id " +
                    "GROUP BY days " +
                    "ORDER BY days",
            nativeQuery = true
    )
    List<UserStatisticByGraphic> getStatisticOfStreamer(@Param("days") int days, @Param("id") Long id);

    @Query(
            value = """
                select
                    count(*) as totalCount,
                    count(case when enable = true then 1 end) as enableTotalCount,
                    count(case when created_at >= CURRENT_DATE then 1 end) as dailyTotalCount,
                    count(case when enable = true and created_at >= CURRENT_DATE then 1 end) as dailyEnableTotalCount
                from users_table
                """,
            nativeQuery = true
    )
    UserStatistic getUsersStatistic();

    @Query(
            value = """
            select
                coalesce(sum(d.commission), 0) as donationCommissionsAmount,
                coalesce(sum(case when w.status = 'COMPLETED' then w.commission end), 0) as withdrawCommissionsAmount,
                coalesce(sum(u.balance), 0) as currentStreamersBalance
            from users_table u
            left join donations_table d on u.id = d.streamer_id
            left join withdraws_table w on u.id = w.streamer_id
            """,
            nativeQuery = true
    )
    ProfitStatistic getProfitStatistic();

    List<UserEntity> getAllByEnableTrue();

    Optional<UserEntity> findByEmailOrUsername(String email, String username);
}