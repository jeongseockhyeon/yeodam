package com.hifive.yeodam.orderdetail.repository;

import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("select od from OrderDetail od " +
            "join fetch od.reservation r " +
            "join fetch r.guide d " +
            "join fetch od.item " +
            "join fetch od.order o " +
            "join fetch o.payment p " +
            "where od.status in :detailStatus " +
            "and o.status != :status " +
            "and o.user = :user ")
    Slice<OrderDetail> findOrderDetailsPending(List<OrderDetailStatus> detailStatus, OrderStatus status, User user, Pageable pageable);

    @Query("select od from OrderDetail od " +
            "where od.status in(:detailStatus) " +
            "and od.order.user =:user")
    Slice<OrderDetail> findOrderDetailsCancelComplete(List<OrderDetailStatus> detailStatus, User user);

    @Query("select case when count(od) > 0 then true else false end " +
            "from OrderDetail od " +
            "join od.reservation r " +
            "where r.guide.guideId = :guideId " +
            "and od.status = :status " +
            "and (r.startDate between :startDate and :endDate " +
            "or r.endDate between :startDate and :endDate)")
    boolean isGuideAvailable(Long guideId, OrderDetailStatus status, LocalDate startDate, LocalDate endDate);

    @Query("select od from OrderDetail od " +
            "join fetch od.item i " +
            "where i.id = :itemId " +
            "and od.order.orderUid = :orderUid")
    Optional<OrderDetail> findByItemOrderUid(Long itemId, String orderUid);

    @Query("select od " +
            "from OrderDetail od " +
            "join fetch od.order o " +
            "join fetch od.reservation r " +
            "where od.status != :status " +
            "and od.item.id = :itemId " +
            "and od.item.seller.companyId = :sellerId")
    Page<OrderDetail> findAllBySeller(OrderDetailStatus status, Long sellerId, Long itemId, Pageable pageable);

    @Query("select od " +
            "from OrderDetail od " +
            "where od.order.status = :orderStatus " +
            "and od.status =: orderDetailStatus " +
            "and od.reservation.startDate <: currentDate")
    List<OrderDetail> findPastReservationDetails(OrderStatus orderStatus, OrderDetailStatus orderDetailStatus, LocalDate currentDate);

    @Modifying
    @Query("update OrderDetail od " +
            "set od.status = :status " +
            "where od.id in :ids")
    void updateStatusBulk(@Param("status") OrderStatus status, @Param("ids") List<Long> ids);
}
