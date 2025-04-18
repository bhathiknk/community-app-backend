package com.communityappbackend.dto.UserDataHandleDTOs;

import lombok.*;

/** Request object for updating user profile fields. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequestDTO {
    private String fullName;
    private String phone;
    private String address;
    private String city;
    private String province;
}
