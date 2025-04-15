package com.communityappbackend.dto;

import lombok.*;
import java.util.List;

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
