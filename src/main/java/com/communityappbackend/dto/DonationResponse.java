package com.communityappbackend.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

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

    // For backward compatibility, keep this simple "images" list of URLs
    private List<String> images;

    // New: Detailed list that includes image IDs
    private List<Map<String, Object>> imageList;
    // Owner info
    private String ownerId;
    private String ownerFullName;
    private String ownerEmail;
    // etc., if needed
}
