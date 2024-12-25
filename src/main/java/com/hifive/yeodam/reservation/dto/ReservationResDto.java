package com.hifive.yeodam.reservation.dto;

import com.hifive.yeodam.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReservationResDto {
    private Long reservationId;
    private String userName;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;

    public ReservationResDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.userName = reservation.getUser().getName();
        this.reservationStartDate = reservation.getReservationStartDate();
        this.reservationEndDate = reservation.getReservationEndDate();
    }
}
