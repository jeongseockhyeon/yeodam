package com.hifive.yeodam.category.dto;

import lombok.Getter;

@Getter
public class CategoryReqDto {
    private final Long parentCategoryId;

    private final String categoryName;

    public CategoryReqDto(Long parentCategoryId, String categoryName) {
        this.parentCategoryId = parentCategoryId;
        this.categoryName = categoryName;
    }
}
