package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.tour.entity.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
    void deleteByGuide(Guide guide);
}
