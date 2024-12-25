package com.hifive.yeodam.reservation.entity;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="guide_id")
    private Guide guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private LocalDate reservationStartDate; //예약 시작일

    private LocalDate  reservationEndDate; //예약 종료일

}
