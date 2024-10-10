package donatehub.service.notification.notification;

import donatehub.domain.entities.NotificationEntity;
import donatehub.domain.exceptions.BaseException;
import donatehub.domain.projections.NotificationInfo;
import donatehub.domain.request.NotificationRequest;
import donatehub.repo.NotificationRepository;
import donatehub.service.notification.user_notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repo;
    private final UserNotificationService userNotificationService;

    @Override
    public void create(NotificationRequest notificationRequest) {
        NotificationEntity saved = repo.save(new NotificationEntity(
                notificationRequest.getTitle(),
                notificationRequest.getMessage()
        ));

        userNotificationService.create(saved);
    }

    @Override
    public void update(Long id, NotificationRequest notificationRequest) {
        NotificationEntity notification = findById(id);

        notification.setMessage(notificationRequest.getMessage());
        notification.setTitle(notificationRequest.getTitle());

        repo.save(notification);
    }

    private NotificationEntity findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new BaseException("Bildirishnoma topilmadi", HttpStatus.NOT_FOUND));
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repo.deleteById(id);
    }

    @Override
    public List<NotificationInfo> getAll() {
        return repo.getAllBy(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
