package com.hifive.yeodam.reservation.repository;

import com.hifive.yeodam.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
}
