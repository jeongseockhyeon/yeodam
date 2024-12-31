package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.category.entity.QCategory;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.QTour;
import com.hifive.yeodam.tour.entity.QTourCategory;
import com.hifive.yeodam.tour.entity.Tour;
import com.querydsl.core.BooleanBuilder;
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

    @Override
    public Slice<Tour> searchByFilterAndActive(Long cursorId, int pageSize, SearchFilterDto searchFilterDto) {

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
        if (searchFilterDto.getMinPrice() != null) {
            builder.and(tour.price.goe(Integer.parseInt(searchFilterDto.getMinPrice())));
        }
        if (searchFilterDto.getMaxPrice() != null) {
            builder.and(tour.price.loe(Integer.parseInt(searchFilterDto.getMaxPrice())));
        }


        List<Tour> results = jpaQueryFactory.select(tour)
                .from(tour)
                .leftJoin(tour.tourCategories, tourCategory)
                .leftJoin(tourCategory.category, category)
                .where(tour.active.isTrue()
                        .and(builder)
                        .and(cursorId != null ? tour.id.lt(cursorId) : null))
                .orderBy(tour.id.desc())
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
        QTour tour = QTour.tour;
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
}
