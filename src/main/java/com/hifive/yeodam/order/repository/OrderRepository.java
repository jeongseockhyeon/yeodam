package com.hifive.yeodam.order.repository;

import com.hifive.yeodam.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.orderDetails where o.orderUid = :orderUid")
    Optional<Order> findByOrderUid(String orderUid);

    List<Order> findByUserId(Long userId);
}
