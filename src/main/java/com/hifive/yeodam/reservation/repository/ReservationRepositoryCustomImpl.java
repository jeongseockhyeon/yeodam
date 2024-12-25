package com.hifive.yeodam.reservation.repository;

import com.hifive.yeodam.reservation.entity.QReservation;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.user.entity.User;
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
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(reservation.item.seller.eq(targetSeller));

        return jpaQueryFactory.select(reservation)
                .from(reservation)
                .where(builder)
                .fetch();
    }

    @Override
    public List<Reservation> findReservationByUser(User targetUser) {
        QReservation reservation = QReservation.reservation;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.user.eq(targetUser));

        return jpaQueryFactory.select(reservation)
                .from(reservation)
                .where(builder)
                .fetch();
    }


}
