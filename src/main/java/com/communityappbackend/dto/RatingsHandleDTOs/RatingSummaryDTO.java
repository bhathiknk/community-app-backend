// src/main/java/com/communityappbackend/dto/RatingSummaryDTO.java
package com.communityappbackend.dto.RatingsHandleDTOs;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingSummaryDTO {
    private double average;
    private int count;
}
