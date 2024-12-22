package com.hifive.yeodam.tour.repository;

import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long>, TourRepositoryCustom {
    List<Tour> findBySeller(Seller seller);
}
