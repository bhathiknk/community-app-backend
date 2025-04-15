// File: ItemResponse.java
package com.communityappbackend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {

    private String itemId;
    private String title;
    private String description;
    private Double price;
    private Long categoryId;
    private List<String> images;

    private String status;
    private String createdAt;

    // Owner info
    private String ownerFullName;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerAddress;

    // NEW: Owner's profile image URL
    private String ownerProfileImage;
}
