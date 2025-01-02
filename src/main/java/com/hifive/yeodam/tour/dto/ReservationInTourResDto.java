package com.hifive.yeodam.tour.dto;

import com.hifive.yeodam.reservation.entity.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationInTourResDto {
    private final Long guideId;
    private final Long reservationId;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public ReservationInTourResDto(Reservation reservation) {
        this.guideId = reservation.getGuide().getGuideId();
        this.reservationId = reservation.getId();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
    }
}
