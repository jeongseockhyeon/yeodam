package com.hifive.yeodam.reservation.entity;

import com.hifive.yeodam.seller.entity.Guide;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;


@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    private LocalDate startDate; //예약 시작일

    private LocalDate endDate; //예약 종료일

    @Builder
    public Reservation(Guide guide, LocalDate reservationStartDate, LocalDate reservationEndDate) {
        this.guide = guide;
        this.startDate = reservationStartDate;
        this.endDate = reservationEndDate;
    }

    public int getRemainingDay() {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), startDate);
    }

    public void changeGuide(Guide guide) {
        this.guide = guide;
    }
}
