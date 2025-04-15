package com.communityappbackend.controller;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.model.Notification;
import com.communityappbackend.model.User;
import com.communityappbackend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles user notifications.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    /**
     * System-internal endpoint to create a notification.
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDTO dto){
        Notification notification = notificationService.createNotification(dto);
        return ResponseEntity.ok(notification);
    }

    /**
     * Returns all notifications for the authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        List<Notification> notifications = notificationService.getNotificationsByUserId(currentUser.getUserId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marks a notification as read/unread.
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> updateNotificationReadStatus(
            @PathVariable String notificationId,
            @RequestParam boolean read,
            Authentication auth
    ) {
        Notification updated = notificationService.updateNotificationReadStatus(notificationId, read);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a notification by ID.
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable String notificationId,
            Authentication auth
    ) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
