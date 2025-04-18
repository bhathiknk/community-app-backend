package com.communityappbackend.dto.UserDataHandleDTOs;

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
