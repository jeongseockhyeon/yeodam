package com.hifive.yeodam.payment.repository;

import com.hifive.yeodam.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p join fetch p.order")
    Optional<Payment> findByOrderId(Long orderId);

    @Query("select p from Payment p " +
            "join fetch p.order o " +
            "join fetch o.user u " +
            "join fetch u.auth " +
            "where p.id = :paymentId")
    Optional<Payment> findByIdFetchJoin(Long paymentId);

    @Query("select p from Payment p join fetch p.order o where o.orderUid = :orderUid")
    Optional<Payment> findByOrderUid(String orderUid);
}
