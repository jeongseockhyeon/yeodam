package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.tour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TourRepository extends JpaRepository<Tour, Long>, TourRepositoryCustom {

}
