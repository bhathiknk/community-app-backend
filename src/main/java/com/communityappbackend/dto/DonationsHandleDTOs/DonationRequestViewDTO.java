package com.communityappbackend.dto.DonationsHandleDTOs;

import lombok.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * A merged view DTO that shows donation request details plus
 * the donation item data (title, images, etc.) and the requester user data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequestViewDTO {

    // Fields from DonationRequest
    private String requestId;
    private String donationId;
    private String message;
    private String status;
    private Timestamp createdAt;

    // Donation item data
    private String donationTitle;
    private List<String> donationImages;  // e.g. up to 5
    private String donationStatus;        // e.g. ACTIVE, RESERVED, DONATED

    // Requester user data
    private String requestedBy;
    private String requesterFullName;
    private String requesterEmail;
    private String requesterPhone;
    private String requesterProfile;  // URL for profile image, if any
}
