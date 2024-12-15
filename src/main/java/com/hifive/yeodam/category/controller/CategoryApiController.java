package com.hifive.yeodam.category.controller;

import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/category")
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody CategoryReqDto categoryReqDto) {
        return ResponseEntity.status(CREATED).body(categoryService.saveCategory(categoryReqDto));
    }


}
