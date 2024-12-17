package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.QTour;
import com.hifive.yeodam.tour.entity.QTourCategory;
import com.hifive.yeodam.tour.entity.Tour;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TourRepositoryCustomImpl implements TourRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tour> searchByFilter(SearchFilterDto searchFilterDto) {
        QTour tour = QTour.tour;
        QTourCategory tourCategory = QTourCategory.tourCategory;

        BooleanBuilder builder = new BooleanBuilder();

        if (searchFilterDto.getKeyword() != null && searchFilterDto.getKeyword().isEmpty()) {
            builder.and(tour.itemName.containsIgnoreCase(searchFilterDto.getKeyword()));
        }
        if (searchFilterDto.getRegion() != null && !searchFilterDto.getRegion().isEmpty()) {
            builder.and(tour.region.eq(searchFilterDto.getRegion()));
        }
        if (searchFilterDto.getPeriod() != null && !searchFilterDto.getPeriod().isEmpty()) {
            builder.and(tour.period.eq(searchFilterDto.getPeriod()));
        }
        if (searchFilterDto.getCategory() != null && searchFilterDto.getCategory().isEmpty()) {
            builder.and(tourCategory.category.name.containsIgnoreCase(searchFilterDto.getCategory()));
        }


        List<Tour> fetch = (List<Tour>) jpaQueryFactory
                .from(tour)
                .leftJoin(tour.tourCategories, tourCategory)
                .where(builder)
                .fetch();
        return fetch;
    }
}
