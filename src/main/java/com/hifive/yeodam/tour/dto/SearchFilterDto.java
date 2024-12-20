package com.hifive.yeodam.tour.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchFilterDto {

    private final List<String> categories; // 다중 카테고리 수용을 위해 List로 변경
    private final String keyword;
    private final String region;
    private final String period;

    public SearchFilterDto(List<String> categories, String keyword, String region, String period) {
        this.categories = categories;
        this.keyword = keyword;
        this.region = region;
        this.period = period;
    }
}
