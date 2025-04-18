// src/main/java/com/communityappbackend/dto/RatingDTO.java
package com.communityappbackend.dto.RatingsHandleDTOs;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingDTO {
    private String donationRequestId;
    private Integer score;
    private String comment;
}
