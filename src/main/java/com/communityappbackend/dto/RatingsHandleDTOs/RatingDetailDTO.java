// src/main/java/com/communityappbackend/dto/RatingDetailDTO.java
package com.communityappbackend.dto.RatingsHandleDTOs;

import lombok.*;
import java.sql.Timestamp;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingDetailDTO {
    private String ratingId;
    private String donationId;
    private String donationTitle;
    private String donationImage;      // first image URL
    private String rateeId;
    private String rateeFullName;
    private String rateeProfileImage;
    private Integer score;
    private String comment;
    private Timestamp createdAt;
}
