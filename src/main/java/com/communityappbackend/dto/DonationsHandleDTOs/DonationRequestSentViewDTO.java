package com.communityappbackend.dto.DonationsHandleDTOs;

import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DonationRequestSentViewDTO {
    private String requestId;
    private String donationId;
    private String donationTitle;
    private List<String> donationImages;
    private String donationStatus;
    private String message;
    private String status;
    private Timestamp createdAt;
    // Receiver (item owner) info
    private String receiverId;
    private String receiverFullName;
    private String receiverEmail;
    private String receiverPhone;
    private String receiverProfile;
}
