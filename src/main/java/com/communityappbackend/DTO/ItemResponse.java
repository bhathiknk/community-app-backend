package com.communityappbackend.DTO;

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

    // NEW: Owner info
    private String ownerFullName;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerAddress;
}
