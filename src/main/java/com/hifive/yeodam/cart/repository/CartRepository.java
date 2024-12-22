package com.hifive.yeodam.cart.repository;

import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndItem(User user, Item item);
}
