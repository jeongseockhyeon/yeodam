package com.hifive.yeodam.order.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.dto.request.CancelOrderRequest;
import com.hifive.yeodam.order.dto.response.CancelOrderResponse;
import com.hifive.yeodam.order.dto.response.CreateOrderResponse;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.orderdetail.service.OrderDetailCommandService;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.*;
import static com.hifive.yeodam.order.domain.OrderStatus.CANCELED;
import static com.hifive.yeodam.order.domain.OrderStatus.FAILED;
import static com.hifive.yeodam.orderdetail.domain.OrderDetailStatus.USED;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderDetailCommandService orderDetailCommandService;
    private final UserRepository userRepository;

    @Transactional
    public CreateOrderResponse order(AddOrderRequest request, Principal principal) {

        User user = getUserByEmail(principal);
        List<OrderDetail> orderDetails = orderDetailCommandService.insertOrderDetails(request);

        int totalPrice = orderDetails.stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();

        Order order = Order.createOrder(user, totalPrice, orderDetails);
        orderRepository.save(order);

        return CreateOrderResponse.builder()
                .orderUid(order.getOrderUid())
                .build();
    }

    @Transactional
    public CancelOrderResponse cancelOrder(CancelOrderRequest request) {

        Order order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        List<OrderDetail> orderDetails = order.getOrderDetails();

        validateOrderStatus(order);
        validateDetailStatus(orderDetails);

        orderDetails.forEach(od -> {
            od.changeStatus(OrderDetailStatus.CANCELED);
            od.getItem().addStock();
        });

        order.chanceOrderStatus(CANCELED);

        return getCancelOrderResponse(order, getCancelPrice(orderDetails));
    }

    private User getUserByEmail(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private void validateDetailStatus(List<OrderDetail> orderDetails) {
        orderDetails.stream()
                .filter(od -> od.getStatus().equals(USED))
                .forEach(od -> {
                    throw new CustomException(ORDER_CAN_NOT_CANCEL);
                });
    }

    private void validateOrderStatus(Order order) {
        if (List.of(FAILED, CANCELED).contains(order.getStatus())) {
            throw new CustomException(ORDER_CAN_NOT_CANCEL);
        }
    }

    private int getCancelPrice(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();
    }

    private CancelOrderResponse getCancelOrderResponse(Order order, int cancelPrice) {
        return CancelOrderResponse.builder()
                .orderUid(order.getOrderUid())
                .totalPrice(cancelPrice)
                .build();
    }
}