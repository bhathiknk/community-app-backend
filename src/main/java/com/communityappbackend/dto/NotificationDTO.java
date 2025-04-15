package com.communityappbackend.dto;

import lombok.*;

/** DTO for creating notifications. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private String userId;
    private String message;
}
