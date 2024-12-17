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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public String order(AddOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다"));

        List<OrderDetail> orderDetails = createOrderDetails(request);

        Order order = Order.createOrder(user.getId(), orderDetails);
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
            Item item = itemRepository.findById(requestItem.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 상품이 없습니다"));

            OrderDetail orderDetail = OrderDetail.create(item.getId(), requestItem.getCount(), item.getPrice());
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }
}
