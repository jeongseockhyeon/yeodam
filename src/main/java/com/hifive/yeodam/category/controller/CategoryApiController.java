package com.hifive.yeodam.category.controller;

import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResDto> addCategory(@RequestBody CategoryReqDto categoryReqDto) {
        return ResponseEntity.status(CREATED).body(categoryService.saveCategory(categoryReqDto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResDto>> getAllCategory() {
        return ResponseEntity.ok(categoryService.findAllCategory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResDto> updateCategory(@PathVariable Long id, @RequestBody CategoryReqDto categoryReqDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryReqDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}
