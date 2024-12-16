package com.hifive.yeodam.order.service;

import com.hifive.yeodam.order.domain.MockItem;
import com.hifive.yeodam.order.domain.MockUser;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.repository.MockItemRepository;
import com.hifive.yeodam.order.repository.MockUserRespiratory;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
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
    private final MockItemRepository itemRepository;
    private final MockUserRespiratory userRespiratory;

    @Transactional
    public String order(AddOrderRequest request) {

        MockUser user = userRespiratory.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다"));

        List<OrderDetail> orderDetails =  null;/*createOrderDetails(request);*/

        Order order = Order.createOrder(user.getUserId(), orderDetails);
        orderRepository.save(order);

        return order.getOrderUid();
    }

    @Transactional
    public void cancelOrder(String orderUid) {
        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 주문이 없습니다"));

        order.cancelOrder();
    }

   /* private List<OrderDetail> createOrderDetails(AddOrderRequest request) {

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (AddOrderRequest.ItemRequest requestItem : request.getItems()) {
            MockItem item = itemRepository.findById(request.getItems())
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 상품이 없습니다"));

            //원래는 item을 반환해야 하지만 아직 상품이 없어 키를 반환
            OrderDetail orderDetail = OrderDetail.create(item.getItemId(), requestItem.getCount(), item.getPrice());
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }*/
}
