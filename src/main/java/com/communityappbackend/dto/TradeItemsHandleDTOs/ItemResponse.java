package com.communityappbackend.dto.TradeItemsHandleDTOs;

import lombok.*;
import java.util.List;

/** Response object representing an item, including images and owner info. */
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
    private String ownerCity;
    private String ownerState;

    // NEW: Owner's profile image URL
    private String ownerProfileImage;
}
