package com.hifive.yeodam.order.service;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public String order(AddOrderRequest request, Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다"));

        List<OrderDetail> orderDetails = createOrderDetails(request);

        validateOrderMessage(request);

        Order order = Order.createOrder(user, request.getBookerName(), request.getPhoneNumber(),
                request.getOrderMessage(),orderDetails);

        orderRepository.save(order);

        return order.getOrderUid();
    }

    @Transactional
    public void cancelOrder(String orderUid) {
        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 주문이 없습니다"));

        order.cancelOrder();
    }

    private List<OrderDetail> createOrderDetails(AddOrderRequest request) {

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (AddOrderRequest.ItemRequest requestItem : request.getItems()) {
            Item item = itemRepository.findById(requestItem.getId())
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 상품이 없습니다"));

            OrderDetail orderDetail = OrderDetail.create(item, requestItem.getCount(), item.getPrice());
            orderDetails.add(orderDetail);
        }

        return orderDetails;
    }

    private void validateOrderMessage(AddOrderRequest request) {
        if (!StringUtils.hasText(request.getOrderMessage())) {
            request.setOrderMessage("메세지 없음");
        }
    }
}
