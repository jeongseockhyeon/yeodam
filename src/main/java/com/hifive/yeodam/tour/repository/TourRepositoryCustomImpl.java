package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.category.entity.QCategory;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.QTour;
import com.hifive.yeodam.tour.entity.QTourCategory;
import com.hifive.yeodam.tour.entity.Tour;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class TourRepositoryCustomImpl implements TourRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QTour tour = QTour.tour;
    QTourCategory tourCategory = QTourCategory.tourCategory;
    QCategory category = QCategory.category;

    @Override
    public Slice<Tour> searchByFilterAndActive(SearchFilterDto searchFilterDto) {

        Long cursorId = searchFilterDto.getCursorId();
        int pageSize = searchFilterDto.getPageSize();



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
        if (searchFilterDto.getMinPrice() != null) {
            builder.and(tour.price.goe(Integer.parseInt(searchFilterDto.getMinPrice())));
        }
        if (searchFilterDto.getMaxPrice() != null) {
            builder.and(tour.price.loe(Integer.parseInt(searchFilterDto.getMaxPrice())));
        }
        NumberPath<Integer> pricePath = tour.price;
        NumberPath<Double> ratePath = tour.rate;
        Integer cursorPrice = searchFilterDto.getCursorPrice();
        Double cursorRate = searchFilterDto.getCursorRate();
        //정렬 조건
        OrderSpecifier<?> sortOrder = null;
        if ("price".equals(searchFilterDto.getSortBy())) {
            sortOrder = setSortOrderAndConditions(builder, pricePath, cursorPrice, searchFilterDto.getCursorId(), searchFilterDto.getOrder());
        } else if ("rate".equals(searchFilterDto.getSortBy())) {
            sortOrder = setSortOrderAndConditions(builder, ratePath, cursorRate, searchFilterDto.getCursorId(), searchFilterDto.getOrder());
        } else {
            sortOrder = tour.id.desc();
        }

        List<Tour> results = jpaQueryFactory.select(tour)
                .from(tour)
                .leftJoin(tour.tourCategories, tourCategory)
                .leftJoin(tourCategory.category, category)
                .where(tour.active.isTrue()
                        .and(builder)
                        .and(cursorId != null ? tour.id.lt(cursorId) : null))
                .orderBy(sortOrder)
                .limit(pageSize + 1)
                .distinct()
                .fetch();

        boolean hasNext = results.size() > pageSize; //이후 데이터 여부 확인
        if(hasNext){
            results.removeLast();
        }
        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }

    @Override
    public Slice<Tour> findBySeller(Long cursorId, int pageSize, Seller targetSeller) {
        BooleanBuilder builder = new BooleanBuilder();
        if (targetSeller != null) {
            builder.and(tour.seller.eq(targetSeller));
        }
        List<Tour> result = jpaQueryFactory.select(tour)
                .from(tour)
                .where(builder)
                .orderBy(tour.id.desc())
                .limit(pageSize + 1)
                .fetch();
        boolean hasNext = result.size() > pageSize;
        if(hasNext){
            result.removeLast();
        }
        return new SliceImpl<>(result, PageRequest.of(0, pageSize), hasNext);
    }

    public <T extends Number & Comparable<?>> OrderSpecifier<?> setSortOrderAndConditions(
            BooleanBuilder builder, NumberPath<T> sortField, T cursorValue, Long cursorId, String order) {
        OrderSpecifier<?> sortOrder;
        if ("asc".equals(order)) {
            sortOrder = sortField.asc();
            if (cursorValue != null) {
                if (cursorId != null) {
                    builder.and(
                            sortField.gt(cursorValue)
                                    .or(sortField.eq(cursorValue).and(tour.id.gt(cursorId)))
                    );
                } else {
                    builder.and(sortField.gt(cursorValue));
                }
            }
        } else {
            sortOrder = sortField.desc();
            if (cursorValue != null) {
                if (cursorId != null) {
                    builder.and(
                            sortField.lt(cursorValue)
                                    .or(sortField.eq(cursorValue).and(tour.id.lt(cursorId)))
                    );
                } else {
                    builder.and(sortField.lt(cursorValue));
                }
            }
        }
        return sortOrder;
        }
}
