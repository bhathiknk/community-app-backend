package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @Column(name = "notification_id", columnDefinition = "CHAR(36)")
    private String notificationId;

    @Column(name = "user_id", nullable = false)
    private String userId; // The user to whom this notification belongs

    @Column(nullable = false)
    private String message;

    // Escape the column name 'read' using backticks to avoid conflicts with reserved words.
    @Column(name = "`read`")
    @Builder.Default
    private Boolean read = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void generateId() {
        if (notificationId == null) {
            this.notificationId = UUID.randomUUID().toString();
        }
    }
}
