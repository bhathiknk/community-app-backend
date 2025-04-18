// src/main/java/com/communityappbackend/dto/RatingsHandleDTOs/RatingReceivedDTO.java
package com.communityappbackend.dto.RatingsHandleDTOs;

import lombok.*;
import java.sql.Timestamp;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingReceivedDTO {
    private String ratingId;
    private String donationId;
    private String donationTitle;
    private String donationImage;      // first image URL
    private String raterId;
    private String raterFullName;
    private String raterProfileImage;
    private Integer score;
    private String comment;
    private Timestamp createdAt;
}
