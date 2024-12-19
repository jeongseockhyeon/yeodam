package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.category.entity.QCategory;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.QTour;
import com.hifive.yeodam.tour.entity.QTourCategory;
import com.hifive.yeodam.tour.entity.Tour;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class TourRepositoryCustomImpl implements TourRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tour> searchByFilter(SearchFilterDto searchFilterDto) {

        QTour tour = QTour.tour;
        QTourCategory tourCategory = QTourCategory.tourCategory;
        QCategory category = QCategory.category;

        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(searchFilterDto.getCategory())) {
            builder.and(category.name.eq(searchFilterDto.getCategory()));
        }


        return jpaQueryFactory.select(tour)
                .from(tour)
                .leftJoin(tour.tourCategories, tourCategory)
                .leftJoin(tourCategory.category, category)
                .where(builder)
                .distinct()
                .fetch();
    }
}
