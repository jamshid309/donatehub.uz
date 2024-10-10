package donatehub.domain.projections;

import donatehub.domain.entities.UserNotificationEntity;

/**
 * Projection for {@link UserNotificationEntity}
 */
public interface UserNotificationInfo {
    Long getId();

    Boolean isViewed();

    NotificationInfo getNotification();
}