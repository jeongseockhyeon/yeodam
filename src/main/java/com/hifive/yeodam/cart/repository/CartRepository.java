package com.hifive.yeodam.cart.repository;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT DISTINCT c FROM Cart c " +
            "LEFT JOIN FETCH c.item i " +
            "LEFT JOIN FETCH i.itemImages img " +
            "LEFT JOIN FETCH c.guide g " +
            "WHERE c.auth = :auth")
    List<Cart> findByAuthWithItemsAndImages(@Param("auth") Auth auth);

    @Query("SELECT DISTINCT c FROM Cart c " +
            "LEFT JOIN FETCH c.item i " +
            "LEFT JOIN FETCH i.itemImages img " +
            "LEFT JOIN FETCH c.guide g " +
            "WHERE c.id IN :cartIds")
    List<Cart> findByIdsWithItemsAndImages(@Param("cartIds") List<Long> cartIds);

    int countByAuth(Auth auth);
    Optional<Cart> findByAuthAndItem(Auth auth, Item item);
    Optional<Cart> findByIdAndAuth(Long id, Auth auth);
}
