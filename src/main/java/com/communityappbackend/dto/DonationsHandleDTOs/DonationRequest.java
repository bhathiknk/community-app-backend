package com.communityappbackend.dto.DonationsHandleDTOs;

import lombok.*;

/**
 * Request body for adding a new donation item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequest {
    private String title;
    private String description;
    // You could add more fields if needed (e.g., location, category, etc.)
}
