package com.hifive.yeodam.category.service;

import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /*카테고리 등록*/
    public CategoryResDto saveCategory(CategoryReqDto categoryReqDto) {

        if (categoryReqDto.getParentCategoryId() == null) {
            Category newCategory = Category.builder()
                    .name(categoryReqDto.getCategoryName())
                    .parent(null)
                    .build();

            Category savedCategory = categoryRepository.save(newCategory);
            return new CategoryResDto(savedCategory);
        }
        Category parentCategory = categoryRepository.findById(categoryReqDto.getParentCategoryId())
                .orElseThrow(()->new RuntimeException("해당 상위 카테고리는 존재하지 않습니다."));

        Category newCategory = Category.builder()
                .name(categoryReqDto.getCategoryName())
                .parent(parentCategory)
                .build();
        Category savedCategory = categoryRepository.save(newCategory);
        return new CategoryResDto(savedCategory);
    }
    /*카테고리 전체 목록 조회*/
    public List<CategoryResDto> findAllCategory() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResDto> categoryResDtoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryResDtoList.add(new CategoryResDto(category));
        }
        return categoryResDtoList;
    }
    /*카테고리 단일 조회*/
    public CategoryResDto findCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));
        return new CategoryResDto(category);
    }
    /*카테고리 수정*/
    public CategoryResDto updateCategory(Long id, CategoryReqDto categoryReqDto) {

        Category targetCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));

        targetCategory.updateCategory(categoryReqDto.getCategoryName());

        return new CategoryResDto(targetCategory);
    }
    /*카테고리 삭제*/
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));

        //하위 카테고리가 존재하는 상위 카테고리 삭제 요청 시 발생하는 오류 추후 예외 처리
        // -> 하위 카테고리가 존재할 경우 상위 카테고리는 삭제되서는 안됨
        categoryRepository.delete(category);
    }

}
