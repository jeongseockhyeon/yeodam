package com.hifive.yeodam.orderdetail.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.orderdetail.repository.OrderDetailRepository;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.*;
import static com.hifive.yeodam.order.dto.request.AddOrderRequest.orderRequest;

@Service
@RequiredArgsConstructor
public class OrderDetailCommandService {

    private final OrderDetailRepository orderDetailRepository;
    private final ReservationRepository reservationRepository;
    private final GuideRepository guideRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public List<OrderDetail> createDetails(AddOrderRequest request) {

        List<orderRequest> orderRequests = request.getOrderRequests();

        List<OrderDetail> orderDetails = new ArrayList<>();

        orderRequests.forEach(orderRequest -> {
            OrderDetail orderDetail = buildOrderDetail(orderRequest);
            Reservation reservation = buildReservation(orderRequest, orderDetail);

            orderDetails.add(orderDetail);

            reservationRepository.save(reservation);
            orderDetailRepository.save(orderDetail);
        });

        return orderDetails;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void processPastReservation() {

        List<Long> ids = orderDetailRepository.findPastReservationDetails(
                        OrderStatus.COMPLETED,
                        OrderDetailStatus.PENDING,
                        LocalDate.now()).stream()
                .map(OrderDetail::getId)
                .toList();

        orderDetailRepository.updateStatusBulk(OrderStatus.COMPLETED, ids);
    }

    private OrderDetail buildOrderDetail(orderRequest request) {

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        if (item.getStock() <= 0) {
            throw new CustomException(NOT_ENOUGH_STOCK);
        }

        validateOrderMessage(request);

        return OrderDetail.builder()
                .item(item)
                .count(request.getCount())
                .price(item.getPrice())
                .bookerName(request.getBookerName())
                .bookerPhone(request.getPhoneNumber())
                .message(request.getOrderMessage())
                .build();
    }

    private Reservation buildReservation(orderRequest request, OrderDetail orderDetail) {

        Guide guide = guideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new CustomException(GUIDE_NOT_FOUND));

        checkGuideAvailability(request, guide);

        return Reservation.builder()
                .guide(guide)
                .orderDetail(orderDetail)
                .reservationStartDate(request.getStartDate())
                .reservationEndDate(request.getEndDate())
                .build();
    }

    private void validateOrderMessage(AddOrderRequest.orderRequest request) {
        if (!StringUtils.hasText(request.getOrderMessage()))
            request.setOrderMessage("메세지 없음");
    }

    private void checkGuideAvailability(orderRequest request, Guide guide) {
        if (orderDetailRepository.isGuideAvailable(guide.getGuideId(), OrderDetailStatus.PENDING, request.getStartDate(), request.getEndDate())) {
            throw new CustomException(RESERVED_GUIDE);
        }
    }
}
