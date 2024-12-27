package com.hifive.yeodam.orderdetail.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.repository.OrderDetailRepository;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.GUIDE_NOT_FOUND;
import static com.hifive.yeodam.global.exception.CustomErrorCode.ITEM_NOT_FOUND;
import static com.hifive.yeodam.order.dto.request.AddOrderRequest.orderRequest;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final ReservationRepository reservationRepository;
    private final GuideRepository guideRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public List<OrderDetail> insertOrderDetails(AddOrderRequest request) {

        List<orderRequest> orderRequests = request.getOrderRequests();

        List<OrderDetail> orderDetails = new ArrayList<>();

        orderRequests.forEach(or -> {
            Reservation reservation = buildReservation(or);
            OrderDetail orderDetail = buildOrderDetail(or, reservation);

            orderDetails.add(orderDetail);

            reservationRepository.save(reservation);
            orderDetailRepository.save(orderDetail);
        });

        return orderDetails;
    }

    private OrderDetail buildOrderDetail(orderRequest request, Reservation reservation) {

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        validateOrderMessage(request);

        return OrderDetail.builder()
                .item(item)
                .count(request.getCount())
                .price(item.getPrice())
                .bookerName(request.getBookerName())
                .bookerPhone(request.getPhoneNumber())
                .message(request.getOrderMessage())
                .reservation(reservation)
                .build();
    }

    private Reservation buildReservation(orderRequest request) {

        Guide guide = guideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new CustomException(GUIDE_NOT_FOUND));

        return Reservation.builder()
                .guide(guide)
                .reservationStartDate(request.getStartDate())
                .reservationEndDate(request.getEndDate())
                .build();
    }

    private void validateOrderMessage(AddOrderRequest.orderRequest request) {
        if (!StringUtils.hasText(request.getOrderMessage()))
            request.setOrderMessage("메세지 없음");
    }
}
