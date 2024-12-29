package com.hifive.yeodam.cart.repository;

import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT DISTINCT c FROM Cart c " +
            "JOIN FETCH c.item i " +
            "JOIN FETCH i.itemImages " +
            "WHERE c.user = :user")
    List<Cart> findByUserWithItemsAndImages(@Param("user") User user);

    @Query("SELECT DISTINCT c FROM Cart c " +
            "JOIN FETCH c.item i " +
            "JOIN FETCH i.itemImages " +
            "WHERE c.id IN :cartIds")
    List<Cart> findByIdsWithItemsAndImages(@Param("cartIds") List<Long> cartIds);

    int countByUser(User user);
    Optional<Cart> findByUserAndItem(User user, Item item);
}
