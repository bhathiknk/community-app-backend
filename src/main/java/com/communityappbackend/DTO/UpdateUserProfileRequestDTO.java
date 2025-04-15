package com.communityappbackend.DTO;

import lombok.*;

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
