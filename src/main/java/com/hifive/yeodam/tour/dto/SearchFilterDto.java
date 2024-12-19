package com.hifive.yeodam.tour.dto;

import lombok.Getter;

@Getter
public class SearchFilterDto {

    private String category;

    public SearchFilterDto(String category) {

        this.category = category;
    }
}
