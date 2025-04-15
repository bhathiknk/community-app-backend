package com.communityappbackend.service;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.exception.NotificationNotFoundException;
import com.communityappbackend.model.Notification;
import com.communityappbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles logic for creating and reading notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public Notification createNotification(NotificationDTO dto) {
        Notification notification = Notification.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .read(false)
                .build();
        return notificationRepo.save(notification);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepo.findByUserId(userId);
    }

    public Notification updateNotificationReadStatus(String notificationId, boolean read) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));
        notification.setRead(read);
        return notificationRepo.save(notification);
    }

    public void deleteNotification(String notificationId) {
        if (!notificationRepo.existsById(notificationId)) {
            throw new NotificationNotFoundException("Notification not found: " + notificationId);
        }
        notificationRepo.deleteById(notificationId);
    }
}
