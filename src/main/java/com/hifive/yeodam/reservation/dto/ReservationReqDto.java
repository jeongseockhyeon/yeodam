package com.hifive.yeodam.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReservationReqDto {
    private Long guideId;
    private Long itemId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationEndDate;
}
