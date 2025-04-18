package com.communityappbackend.dto.TradeItemsHandleDTOs;

import lombok.*;

/** Request object when creating a new item. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    private String title;
    private String description;
    private Double price;
    private Long categoryId;
}
