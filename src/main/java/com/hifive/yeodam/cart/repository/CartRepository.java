package com.hifive.yeodam.cart.repository;

import com.hifive.yeodam.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndItem(User user, Item item);
}
