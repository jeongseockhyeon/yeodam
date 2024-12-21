package com.hifive.yeodam.category.dto;

import com.hifive.yeodam.category.entity.Category;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CategoryResDto {
    private Long id;
    private Long parentId;
    private String name;
    private List<CategoryResDto> children;

    public CategoryResDto(Category category) {
        this.id = category.getId();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;
        this.name = category.getName();
        this.children = category.getChildren() != null
                ? category.getChildren().stream().map(CategoryResDto::new)
                .collect(Collectors.toList()) : null;
    }
}
