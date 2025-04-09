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

    private String status;   // new: show item status
    private String createdAt; // new: string form of timestamp
}
