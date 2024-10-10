package donatehub.service.notification.user_notification;

import donatehub.domain.entities.NotificationEntity;
import donatehub.domain.projections.UserNotificationInfo;

import java.util.List;

public interface UserNotificationService {
    void create(NotificationEntity notification);

    void markAllAsRead(Long userId);

    List<UserNotificationInfo> getAllNotificationsOfUser(Long userId);
}
