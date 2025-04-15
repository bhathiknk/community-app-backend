package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private String userId;  // We'll assign UUID manually

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(columnDefinition = "TEXT")
    private String address;

    // NEW: city and province
    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "is_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private java.sql.Timestamp createdAt;

    // A convenience method to generate userId if it's null
    @PrePersist
    public void generateUserId() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID().toString();
        }
    }
}
