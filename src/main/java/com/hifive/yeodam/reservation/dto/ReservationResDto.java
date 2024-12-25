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
    private String itemName;
    private String sellerName;
    private String guideName;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;

    public ReservationResDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.userName = reservation.getUser().getName();
        this.itemName = reservation.getItem().getItemName();
        this.sellerName = reservation.getItem().getSeller().getCompanyName();
        this.guideName = reservation.getGuide().getName();
        this.reservationStartDate = reservation.getReservationStartDate();
        this.reservationEndDate = reservation.getReservationEndDate();
    }
}
