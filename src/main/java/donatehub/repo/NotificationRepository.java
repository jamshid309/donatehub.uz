package donatehub.repo;

import donatehub.domain.entities.NotificationEntity;
import donatehub.domain.projections.NotificationInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationInfo> getAllBy(Sort sort);
}