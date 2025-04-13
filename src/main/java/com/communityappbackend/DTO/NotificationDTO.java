package com.communityappbackend.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private String userId;   // For internal use
    private String message;
}
