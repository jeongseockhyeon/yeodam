package com.hifive.yeodam.reservation.repository;

import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.user.entity.User;

import java.util.List;

public interface ReservationRepositoryCustom {
    List<Reservation> findReservationBySeller(Seller seller);
    List<Reservation> findReservationByUser(User user);
}
