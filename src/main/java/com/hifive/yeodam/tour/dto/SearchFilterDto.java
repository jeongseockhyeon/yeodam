package com.hifive.yeodam.tour.dto;

import lombok.Getter;

@Getter
public class SearchFilterDto {

    private final String category;

    private final String keyword;

    private final String region;

    private final String period;

    public SearchFilterDto(String category, String keyword, String region, String period) {

        this.category = category;
        this.keyword = keyword;
        this.region = region;
        this.period = period;
    }
}
