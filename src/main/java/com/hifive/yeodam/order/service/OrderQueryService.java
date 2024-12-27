package com.hifive.yeodam.order.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.dto.response.AfterOrderResponse;
import com.hifive.yeodam.order.dto.response.BeforeOrderResponse;
import com.hifive.yeodam.order.dto.response.OrderListResponse;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.exception.UserException;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.ORDER_NOT_FOUND;
import static com.hifive.yeodam.order.domain.OrderStatus.FAILED;
import static com.hifive.yeodam.orderdetail.domain.OrderDetailsStatus.*;
import static com.hifive.yeodam.user.exception.UserErrorResult.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public OrderListResponse findOrders(int beforeLimit, int afterLimit, Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Pageable beforePageable = Pageable.ofSize(beforeLimit);
        Pageable afterPageable = Pageable.ofSize(afterLimit);

        SliceImpl<BeforeOrderResponse> beforeOrderResponse = getBeforeOrderResponses(user, beforePageable);
        SliceImpl<AfterOrderResponse> afterOrderResponse = getAfterOrderResponses(user, afterPageable);

        return new OrderListResponse(beforeOrderResponse, afterOrderResponse);
    }

    //재결제시 사용
    @Transactional(readOnly = true)
    public AddOrderRequest changeToAddOrderRequest(String orderUid) {

        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        return getAddOrderRequest(order);
    }

    private SliceImpl<BeforeOrderResponse> getBeforeOrderResponses(User user, Pageable beforePageable) {

        Slice<OrderDetail> beforePage = orderRepository
                .findOrderByDetailStatus(List.of(PENDING), FAILED, user, beforePageable);

        List<BeforeOrderResponse> beforeOrderResponses = beforePage.stream()
                .map(BeforeOrderResponse::new)
                .toList();

        return new SliceImpl<>(beforeOrderResponses, beforePageable, beforePage.hasNext());
    }

    private SliceImpl<AfterOrderResponse> getAfterOrderResponses(User user, Pageable afterPageable) {

        Slice<OrderDetail> afterPage = orderRepository
                .findOrderByDetailStatus(List.of(USED, CANCELED), FAILED, user, afterPageable);

        List<AfterOrderResponse> afterOrderResponses = afterPage.stream()
                .map(AfterOrderResponse::new)
                .toList();

        return new SliceImpl<>(afterOrderResponses, afterPageable, afterPage.hasNext());
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
                        .build())
                .toList());
    }
}