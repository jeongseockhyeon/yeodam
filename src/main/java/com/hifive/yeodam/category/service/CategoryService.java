package com.hifive.yeodam.category.service;

import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /*카테고리 등록*/
    public Category saveCategory(CategoryReqDto categoryReqDto) {

        if (categoryReqDto.getParentCategoryId() == null) {
            Category newCategory = Category.builder()
                    .name(categoryReqDto.getCategoryName())
                    .parent(null)
                    .build();

            return categoryRepository.save(newCategory);
        }
        Category parentCategory = categoryRepository.findById(categoryReqDto.getParentCategoryId())
                .orElseThrow(()->new RuntimeException("해당 상위 카테고리는 존재하지 않습니다."));

        Category newCategory = Category.builder()
                .name(categoryReqDto.getCategoryName())
                .parent(parentCategory)
                .build();

        return categoryRepository.save(newCategory);
    }
    /*카테고리 전체 목록 조회*/
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }
    /*카테고리 단일 조회*/
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 카테고리는 존재하지 않습니다."));
    }
    /*카테고리 수정*/
    public Category updateCategory(Long id, CategoryReqDto categoryReqDto) {

        Category targetCategory = categoryRepository.findById(id)
                .orElseThrow(()->new RuntimeException("해당 카테고리는 존재하지 않습니다."));

        targetCategory.updateCategory(categoryReqDto.getCategoryName());
        return categoryRepository.save(targetCategory);
    }

}
