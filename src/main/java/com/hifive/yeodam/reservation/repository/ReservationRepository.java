package com.hifive.yeodam.reservation.repository;

import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.seller.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuide(Guide guide);
}
