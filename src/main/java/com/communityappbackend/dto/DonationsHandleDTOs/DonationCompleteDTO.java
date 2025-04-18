// src/main/java/com/communityappbackend/dto/DonationCompleteDTO.java
package com.communityappbackend.dto.DonationsHandleDTOs;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DonationCompleteDTO {
    private String donationId;
    private String title;
    private List<String> images;
    private String donorId;
    private String donorFullName;
    private String donorEmail;
    private String donorPhone;
    private String donorProfileImage;
}
