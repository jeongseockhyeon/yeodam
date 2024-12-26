/*
package com.hifive.yeodam.order.domain;


import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    User user;
    List<OrderDetail> orderDetails = new ArrayList<>();

    @BeforeEach
    void setup() {
        user = User.builder()
                .name("홍길동")
                .build();

        Tour item = Tour.builder()
                .itemName("testItemA")
                .price(10000)
                .build();

        orderDetails.add(OrderDetail.create(
                item, 2, item.getPrice(), user.getName(), user.getPhone(), "안녕"));
    }

    @AfterEach
    void clear() {
        orderDetails.clear();
    }

    @Test
    void 주문상태_테스트() {

        //처음 생성 시 상태는 PENDING
        Order order = Order.createOrder(user, orderDetails);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        //상품을 이용시 COMPLETED
        order.chanceOrderStatus(OrderStatus.COMPLETED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        //취소 시 CANCELED
        order.chanceOrderStatus(OrderStatus.CANCELED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void getTotalPriceTest_단건_테스트() {

        //given
        Order order = Order.createOrder(user, orderDetails);

        //when
        int totalPrice = order.getTotalPrice();

        //then
        OrderDetail orderDetail = orderDetails.getFirst();
        assertThat(totalPrice).isEqualTo(orderDetail.getTotalPrice());
    }

    @Test
    void getTotalPriceTest_복수_테스트() {

        //given
        Tour item = Tour.builder()
                .itemName("testItem")
                .price(20000)
                .build();

        orderDetails.add(
                OrderDetail.create(item, 2, item.getPrice(), user.getName(), user.getPhone(), "ㅎㅇㅎㅇ"));

        Order order = Order.createOrder(user, orderDetails);

        //when
        int totalPrice = order.getTotalPrice();

        //then
        int orderDetailTotalPrice = orderDetails.stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();
        assertThat(totalPrice).isEqualTo(orderDetailTotalPrice);
    }

    @Test
    @DisplayName("주문상품이 단건일 경우 '아이템이름'만 나와야 한다")
    void 주문상품_대표이름_단건_테스트() {

        //given
        Order order = Order.createOrder(user, orderDetails);

        //when
        String itemSummary = order.getItemSummary();

        //then
        assertThat(itemSummary).isEqualTo(orderDetails.getFirst().getItem().getItemName());
    }

    @Test
    @DisplayName("주문상품이 여러 건 일 경우 '아이템이름 외 n 건'이라고 나와야 한다")
    void 주문상품_대표이름_복수_테스트() {

        //given
        Tour item = Tour.builder()
                .itemName("testItemB")
                .price(20000)
                .build();

        orderDetails.add(OrderDetail.create(
                item, 2, item.getPrice(), user.getName(), user.getPhone(), "ㅎㅇㅎㅇ"));

        Order order = Order.createOrder(user, orderDetails);

        //when
        String itemSummary = order.getItemSummary();

        //then
        String result = orderDetails.getFirst().getItem().getItemName() + " 외 " + orderDetails.size() + "건";
        assertThat(itemSummary).isEqualTo(result);
    }
}*/
