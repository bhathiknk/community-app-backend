package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Holds the file path for an uploaded donation item image.
 */
@Entity
@Table(name = "donation_item_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imagePath; // e.g. "Donations/<uuid>_filename.jpg"

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private DonationItem donationItem;
}
