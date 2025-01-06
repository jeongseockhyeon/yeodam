package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.category.entity.QCategory;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.QTour;
import com.hifive.yeodam.tour.entity.QTourCategory;
import com.hifive.yeodam.tour.entity.Tour;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
@Slf4j
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
        if (hasText(searchFilterDto.getRegion())) {
            builder.and(tour.region.eq(searchFilterDto.getRegion()));
        }
        if (hasText(searchFilterDto.getKeyword())) {
            builder.and(tour.itemName.containsIgnoreCase(searchFilterDto.getKeyword()));
        }
        if (hasText(searchFilterDto.getPeriod())) {
            builder.and(tour.period.eq(searchFilterDto.getPeriod()));
        }
        if (searchFilterDto.getMinPrice() != null) {
            builder.and(tour.price.goe(Integer.parseInt(searchFilterDto.getMinPrice())));
        }
        if (searchFilterDto.getMaxPrice() != null) {
            builder.and(tour.price.loe(Integer.parseInt(searchFilterDto.getMaxPrice())));
        }

        String sortBy = searchFilterDto.getSortBy();
        String order = searchFilterDto.getOrder();

        Integer cursorPrice = searchFilterDto.getCursorPrice();
        Double cursorRate = searchFilterDto.getCursorRate();
        OrderSpecifier<?> primarySort = null;
        OrderSpecifier<?> secondarySort = tour.id.desc();

        if ("price".equals(sortBy)) {
            primarySort = "asc".equals(order) ? tour.price.asc() : tour.price.desc();
            if (cursorPrice != null && cursorId != null) {
                if ("asc".equals(order)) {
                    builder.and(
                            tour.price.gt(cursorPrice)
                                    .or(tour.price.eq(cursorPrice).and(tour.id.lt(cursorId)))
                    );
                } else {
                    builder.and(
                            tour.price.lt(cursorPrice)
                                    .or(tour.price.eq(cursorPrice).and(tour.id.lt(cursorId)))
                    );
                }
            }
        } else if ("rate".equals(sortBy)) {
            primarySort = "asc".equals(order) ? tour.rate.asc() : tour.rate.desc();

            if (cursorRate != null && cursorId != null) {
                if ("asc".equals(order)) {
                    builder.and(
                            tour.rate.gt(cursorRate)
                                    .or(tour.rate.eq(cursorRate).and(tour.id.lt(cursorId)))
                    );
                } else {
                    builder.and(
                            tour.rate.lt(cursorRate)
                                    .or(tour.rate.eq(cursorRate).and(tour.id.lt(cursorId)))
                    );
                }
            }
        } else {
            primarySort = tour.id.desc();
            if (cursorId != null) {
                builder.and(tour.id.lt(cursorId));
            }
        }

        List<Tour> results = jpaQueryFactory.select(tour)
                .from(tour)
                .leftJoin(tour.tourCategories, tourCategory)
                .leftJoin(tourCategory.category, category)
                .where(tour.active.isTrue()
                        .and(builder))
                .orderBy(primarySort, secondarySort)
                .limit(pageSize + 1)
                .distinct()
                .fetch();

        boolean hasNext = results.size() > pageSize;
        if (hasNext) {
            results.remove(results.size() - 1);
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
        if (hasNext) {
            result.removeLast();
        }
        return new SliceImpl<>(result, PageRequest.of(0, pageSize), hasNext);
    }
}