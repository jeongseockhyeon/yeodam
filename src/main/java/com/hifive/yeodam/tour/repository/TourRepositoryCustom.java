package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.Tour;

import java.util.List;

public  interface TourRepositoryCustom {
    List<Tour> searchByFilter(SearchFilterDto searchFilterDto);

}
