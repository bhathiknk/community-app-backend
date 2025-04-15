package com.communityappbackend.service;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.model.Notification;
import com.communityappbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<Notification> opt = notificationRepo.findById(notificationId);
        if (opt.isPresent()) {
            Notification notification = opt.get();
            notification.setRead(read);
            return notificationRepo.save(notification);
        }
        throw new RuntimeException("Notification not found");
    }

    public void deleteNotification(String notificationId) {
        notificationRepo.deleteById(notificationId);
    }
}
