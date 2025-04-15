package com.communityappbackend.dto;

import lombok.*;

/**
 * Response object representing a category.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
}
