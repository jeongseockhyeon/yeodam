package com.hifive.yeodam.tour.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchFilterDto {

    private final List<String> categories;
    private final String keyword;
    private final String region;
    private final String period;
    private final String minPrice;
    private final String maxPrice;

    /*정렬 및 페이지네이션 파라미터*/
    private final Long cursorId;
    private final Integer cursorPrice;
    private final Double cursorRate;
    private final Integer cursorReviewsCount;
    private final int pageSize;
    private final String sortBy;
    private final String order;


    public SearchFilterDto(List<String> categories,
                           String keyword,
                           String region,
                           String period,
                           String minPrice,
                           String maxPrice,
                           String sortBy,
                           String order,
                           Long cursorId,
                           Integer cursorPrice,
                           Double cursorRate,
                           Integer cursorReviewsCount,
                           int pageSize) {

        this.categories = categories;
        this.keyword = keyword;
        this.region = region;
        this.period = period;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sortBy = sortBy;
        this.order = order;
        this.cursorId = cursorId;
        this.cursorPrice = cursorPrice;
        this.cursorRate = cursorRate;
        this.cursorReviewsCount = cursorReviewsCount;
        this.pageSize = pageSize;
    }
}
