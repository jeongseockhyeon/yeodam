package com.hifive.yeodam.reservation.repository;

import com.hifive.yeodam.reservation.entity.QReservation;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.seller.entity.QSeller;
import com.hifive.yeodam.seller.entity.Seller;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Reservation> findReservationBySeller(Seller targetSeller) {
        QReservation reservation = QReservation.reservation;
        QSeller seller = QSeller.seller;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(reservation.item.seller.eq(seller));

        return jpaQueryFactory.select(reservation)
                .from(reservation)
                .where(builder)
                .fetch();
    }
}
