// UserProfileImage.java
package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_profile_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A foreign key to the user table. (Assuming user_id is a CHAR(36))
    @Column(name = "user_id", columnDefinition = "CHAR(36)", nullable = false)
    private String userId;

    // Store the relative file path (or URL) for the profile image.
    @Column(name = "image_path", columnDefinition = "TEXT")
    private String imagePath;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;
}
