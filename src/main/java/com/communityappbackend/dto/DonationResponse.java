package com.communityappbackend.dto;

import lombok.*;

import java.util.List;

/**
 * Response object for donation items.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationResponse {
    private String donationId;
    private String title;
    private String description;
    private String status;
    private String createdAt;

    // For images
    private List<String> images;

    // Owner info
    private String ownerId;
    private String ownerFullName;
    private String ownerEmail;
    // etc., if needed
}
