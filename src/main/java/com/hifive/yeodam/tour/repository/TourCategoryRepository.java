package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.tour.entity.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourCategoryRepository extends JpaRepository<TourCategory, Long> {
    void deleteByCategoryId(Long categoryId);
}
