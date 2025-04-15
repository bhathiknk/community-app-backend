package com.communityappbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;   // plain text from client
    private String address;

    // NEW: city and province
    private String city;
    private String province;
}
