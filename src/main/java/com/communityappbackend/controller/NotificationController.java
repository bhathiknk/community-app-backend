package com.communityappbackend.controller;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.model.Notification;
import com.communityappbackend.service.NotificationService;
import com.communityappbackend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    // Create a new notification (for system internal use)
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDTO dto){
        Notification notification = notificationService.createNotification(dto);
        return ResponseEntity.ok(notification);
    }

    // Get notifications for the currently authenticated user using JWT
    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication auth) {
        // Extract the current authenticated user
        User currentUser = (User) auth.getPrincipal();
        List<Notification> notifications = notificationService.getNotificationsByUserId(currentUser.getUserId());
        return ResponseEntity.ok(notifications);
    }

    // Update notification read/unread status
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> updateNotificationReadStatus(@PathVariable String notificationId,
                                                                     @RequestParam boolean read,
                                                                     Authentication auth) {
        // Optionally, you should check that the notification belongs to the current user.
        Notification updated = notificationService.updateNotificationReadStatus(notificationId, read);
        return ResponseEntity.ok(updated);
    }

    // Delete a notification – optionally check ownership based on auth
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String notificationId,
                                                   Authentication auth) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
