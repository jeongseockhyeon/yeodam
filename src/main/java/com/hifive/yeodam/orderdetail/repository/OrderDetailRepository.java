package com.hifive.yeodam.orderdetail.repository;

import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("select od from OrderDetail od " +
            "join fetch od.item " +
            "join fetch od.order o " +
            "join fetch o.payment p " +
            "where od.status in :detailsStatus " +
            "and o.status != :status " +
            "and o.user = :user ")
    Slice<OrderDetail> findOrderByDetailStatus(List<OrderDetailStatus> detailsStatus, OrderStatus status, User user, Pageable pageable);

    @Query("select case when count(o) > 0 then true else false end " +
            "from OrderDetail o " +
            "join o.reservation r " +
            "where r.guide.guideId = :guideId " +
            "and (r.startDate between :startDate and :endDate " +
            "or r.endDate between :startDate and :endDate)")
    boolean isGuideAvailable(Long guideId, LocalDate startDate, LocalDate endDate);
}
