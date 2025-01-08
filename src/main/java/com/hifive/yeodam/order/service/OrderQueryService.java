package com.hifive.yeodam.order.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hifive.yeodam.global.exception.CustomErrorCode.ORDER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final SellerRepository sellerRepository;

    //재결제시 사용
    @Transactional(readOnly = true)
    public AddOrderRequest changeToAddOrderRequest(String orderUid) {

        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        return getAddOrderRequest(order);
    }

    private AddOrderRequest getAddOrderRequest(Order order) {
        return new AddOrderRequest(order.getOrderDetails().stream()
                .map(od -> AddOrderRequest.orderRequest.builder()
                        .itemId(od.getItem().getId())
                        .name(od.getItem().getItemName())
                        .count(od.getCount())
                        .price(od.getPrice())
                        .bookerName(od.getBookerName())
                        .phoneNumber(od.getBookerPhone())
                        .orderMessage(od.getMessage())
                        .startDate(od.getReservation().getStartDate())
                        .endDate(od.getReservation().getEndDate())
                        .guideId(od.getReservation().getGuide().getGuideId())
                        .build())
                .toList());
    }
}