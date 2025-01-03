package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.entity.Tour;
import org.springframework.data.domain.Slice;

public  interface TourRepositoryCustom {
    Slice<Tour> searchByFilterAndActive(SearchFilterDto searchFilterDto);
    Slice<Tour> findBySeller(Long cursorId, int pageSize,Seller seller);
}
