package com.communityappbackend.dto.UserDataHandleDTOs;

import lombok.*;

/** Request object for sign-up/registration. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String address;

    // NEW: city and province
    private String city;
    private String province;
}
