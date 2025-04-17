// src/main/java/com/communityappbackend/model/Rating.java
package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "ratings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rating {
    @Id
    @Column(name = "rating_id", columnDefinition = "CHAR(36)")
    private String ratingId;

    @Column(name = "donation_request_id", columnDefinition = "CHAR(36)", nullable = false)
    private String donationRequestId;

    @Column(name = "rater_id", columnDefinition = "CHAR(36)", nullable = false)
    private String raterId;

    @Column(name = "ratee_id", columnDefinition = "CHAR(36)", nullable = false)
    private String rateeId;

    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void generateId() {
        if (this.ratingId == null) {
            this.ratingId = UUID.randomUUID().toString();
        }
    }
}
