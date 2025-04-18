package com.communityappbackend.dto.DonationsHandleDTOs;

import lombok.*;
import java.util.List;
import java.util.Map;

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

    // Simple list of image URLs
    private List<String> images;

    // Detailed map: imageId + url
    private List<Map<String, Object>> imageList;

    // Owner info
    private String ownerId;
    private String ownerFullName;
    private String ownerEmail;

    // NEW fields for owner contact
    private String ownerPhone;
    private String ownerAddress;
    private String ownerCity;
    private String ownerProvince;

    // NEW field for owner profile image
    private String ownerProfileImage;
}
