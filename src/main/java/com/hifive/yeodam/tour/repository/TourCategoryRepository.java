package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourCategoryRepository extends JpaRepository<TourCategory, Long> {
    boolean existsByTourAndCategory(Tour targetTour, Category category);
    @Modifying
    @Query("DELETE FROM TourCategory tc WHERE tc.tour = :tour AND tc.category = :category")
    void deleteByTourAndCategory(@Param("tour") Tour tour, @Param("category") Category category);
}
