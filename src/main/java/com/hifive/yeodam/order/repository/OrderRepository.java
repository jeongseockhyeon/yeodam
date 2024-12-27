package com.hifive.yeodam.order.repository;

import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailsStatus;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.orderDetails where o.orderUid = :orderUid")
    Optional<Order> findByOrderUid(String orderUid);
}
