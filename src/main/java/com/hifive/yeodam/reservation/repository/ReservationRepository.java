package com.hifive.yeodam.reservation.repository;

import com.hifive.yeodam.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select case when count (r) > 0 then true else false end " +
            "from Reservation r " +
            "where r.guide.guideId = :guideId " +
            "and  r.startDate between :startDate and :endDate " +
            "or r.endDate between  :startDate and :endDate")
    boolean isGuideAvailable(Long guideId, LocalDate startDate, LocalDate endDate);
}
