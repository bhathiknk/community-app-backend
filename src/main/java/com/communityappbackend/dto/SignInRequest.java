package com.communityappbackend.dto;

import lombok.*;

/** Request object for sign-in. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInRequest {
    private String email;
    private String password;
}
