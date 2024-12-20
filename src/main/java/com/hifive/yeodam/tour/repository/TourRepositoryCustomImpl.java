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
        if (searchFilterDto.getCategories() != null && !searchFilterDto.getCategories().isEmpty()) {
            builder.and(category.name.in(searchFilterDto.getCategories()));
        }
        if (hasText(searchFilterDto.getRegion())){
            builder.and(tour.region.eq(searchFilterDto.getRegion()));
        }
        if (hasText(searchFilterDto.getKeyword())){
            builder.and(tour.itemName.containsIgnoreCase(searchFilterDto.getKeyword()));
        }
        if (hasText(searchFilterDto.getPeriod())){
            builder.and(tour.period.eq(searchFilterDto.getPeriod()));
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
