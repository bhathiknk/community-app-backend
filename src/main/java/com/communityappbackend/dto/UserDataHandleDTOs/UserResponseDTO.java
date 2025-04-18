package com.communityappbackend.dto.UserDataHandleDTOs;

import lombok.*;

/** Response object representing a user's profile. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String province;
}
