package com.communityappbackend.dto.DonationsHandleDTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequestDTO {
    private String donationId;
    private String message;
}
