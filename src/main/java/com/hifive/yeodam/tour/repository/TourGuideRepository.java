package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.tour.entity.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
    @Modifying
    @Query("DELETE FROM TourGuide tg WHERE tg.tour = :tour AND tg.guide = :guide")
    void deleteByTourAndGuide(Tour tour, Guide guide);

    @Modifying
    @Query("DELETE FROM TourGuide tg WHERE tg.guide.guideId = :guideId")
    void deleteByGuideId(Long guideId);

    @Query("SELECT tg.guide.guideId FROM TourGuide tg JOIN tg.guide g WHERE g.seller.companyId = :companyId")
    List<Long> findGuideIdsByCompanyId(Long companyId);
}
