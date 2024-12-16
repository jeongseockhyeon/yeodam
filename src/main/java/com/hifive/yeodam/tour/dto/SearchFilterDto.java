package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFilterDto {

    private String keyword;

    private String region;

    private String period;

    private String category;
}
