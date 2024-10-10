package donatehub.controller;

import donatehub.domain.projections.NotificationInfo;
import donatehub.domain.projections.UserNotificationInfo;
import donatehub.domain.request.NotificationRequest;
import donatehub.service.notification.notification.NotificationService;
import donatehub.service.notification.user_notification.UserNotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserNotificationService userNotificationService;

    @PostMapping
    public void create(@RequestBody NotificationRequest notificationRequest){
        notificationService.create(notificationRequest);
    }

    @PutMapping("/{notificationId}")
    public void update(@PathVariable("notificationId") Long id, @RequestBody NotificationRequest notificationRequest){
        notificationService.update(id, notificationRequest);
    }

    @DeleteMapping("/{notificationId}")
    public void delete(@PathVariable("notificationId") Long id){
        notificationService.delete(id);
    }

    @GetMapping
    public List<NotificationInfo> getAll(){
        return notificationService.getAll();
    }

    @PutMapping("/mark")
    public void markAllAsRead(Long userId){
        userNotificationService.markAllAsRead(userId);
    }

    @GetMapping("/{userId}")
    public List<UserNotificationInfo> getAllNotificationsOfUser(@PathVariable Long userId){
        return userNotificationService.getAllNotificationsOfUser(userId);
    }
}