package com.hifive.yeodam.order.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.dto.request.CancelOrderRequest;
import com.hifive.yeodam.order.dto.response.CancelOrderResponse;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailsStatus;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.*;
import static com.hifive.yeodam.global.exception.CustomErrorCode.NOT_ENOUGH_STOCK;
import static com.hifive.yeodam.order.domain.OrderStatus.CANCELED;
import static com.hifive.yeodam.order.domain.OrderStatus.FAILED;
import static com.hifive.yeodam.orderdetail.domain.OrderDetailsStatus.USED;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public String order(AddOrderRequest request, Principal principal) {

        User user = getUserByEmail(principal);
        List<OrderDetail> orderDetails = createOrderDetails(request);
        int totalPrice = orderDetails.stream().mapToInt(OrderDetail::getTotalPrice).sum();
        Order order = Order.createOrder(user, totalPrice, orderDetails);

        orderRepository.save(order);

        return order.getOrderUid();
    }

    @Transactional
    public CancelOrderResponse cancelOrder(CancelOrderRequest request) {

        Order order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        List<OrderDetail> orderDetails = order.getOrderDetails();

        if (List.of(FAILED, CANCELED).contains(order.getStatus())) {
            throw new CustomException(ORDER_CAN_NOT_CANCEL);
        }

        orderDetails.stream()
                .filter(od -> od.getStatus().equals(USED))
                .forEach(od -> {
                    throw new CustomException(ORDER_CAN_NOT_CANCEL);
                });

        orderDetails.forEach(od -> od.changeStatus(OrderDetailsStatus.CANCELED));
        order.chanceOrderStatus(CANCELED);

        int cancelPrice = orderDetails.stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();

        return CancelOrderResponse.builder()
                .orderUid(order.getOrderUid())
                .totalPrice(cancelPrice)
                .build();
    }

    private User getUserByEmail(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("일치하는_회원이_없습니다"));
    }

    private List<OrderDetail> createOrderDetails(AddOrderRequest orderRequests) {

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (AddOrderRequest.orderRequest request : orderRequests.getOrderRequests()) {

            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

            if (item.getStock() == 0) {
                throw new CustomException(NOT_ENOUGH_STOCK);
            }

            item.removeStock();
            validateOrderMessage(request);

            OrderDetail orderDetail = OrderDetail.create(
                    item, request.getCount(), item.getPrice(),
                    request.getBookerName(), request.getPhoneNumber(), request.getOrderMessage());

            orderDetails.add(orderDetail);
        }

        return orderDetails;
    }

    private void validateOrderMessage(AddOrderRequest.orderRequest request) {
        if (!StringUtils.hasText(request.getOrderMessage()))
            request.setOrderMessage("메세지 없음");
    }
}
