package com.hifive.yeodam.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryReqDto {
    private Long parentCategoryId;

    private String categoryName;
}
