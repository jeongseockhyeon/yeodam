package com.hifive.yeodam.orderdetail.repository;

import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
