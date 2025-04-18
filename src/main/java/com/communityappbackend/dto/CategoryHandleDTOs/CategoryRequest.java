package com.communityappbackend.dto.CategoryHandleDTOs;

import lombok.*;

/**
 * Request object for creating a new category.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    private String categoryName;
}
