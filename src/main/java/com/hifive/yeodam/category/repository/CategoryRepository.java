package com.hifive.yeodam.category.repository;

import com.hifive.yeodam.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
