package com.communityappbackend.DTO;

import lombok.*;

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
}
