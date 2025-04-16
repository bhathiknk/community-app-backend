package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a donation item that users can post.
 */
@Entity
@Table(name = "donation_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationItem {

    @Id
    @Column(name = "donation_id", columnDefinition = "CHAR(36)")
    private String donationId;

    private String title;
    private String description;

    private String ownerId; // user_id from JWT

    @Builder.Default
    private String status = "ACTIVE"; // could be "ACTIVE", "CLOSED", etc.

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;

    // One donation can have multiple images
    @OneToMany(mappedBy = "donationItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DonationItemImage> images = new ArrayList<>();

    @PrePersist
    public void generateDonationId() {
        if (this.donationId == null) {
            this.donationId = UUID.randomUUID().toString();
        }
    }
}
