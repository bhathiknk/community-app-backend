package com.communityappbackend.dto;

import lombok.*;

/**
 * Response object for sign-in and sign-up operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String message;
    private String token; // JWT
}
