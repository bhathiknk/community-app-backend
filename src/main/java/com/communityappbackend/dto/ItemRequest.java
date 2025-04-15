package com.communityappbackend.dto;

import lombok.*;

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
