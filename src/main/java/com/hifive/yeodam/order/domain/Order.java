package com.hifive.yeodam.order.domain;

import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hifive.yeodam.order.domain.OrderStatus.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = PROTECTED)
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO 추후 USER로 변경
    private Long userId;

    @Enumerated(STRING)
    private OrderStatus status;

    //주문 정보 추가
    private String orderUid;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private Order(Long userId, List<OrderDetail> orderDetails) {
        this.userId = userId;
        this.status = PENDING;
        this.orderUid = createOrderUid();
        orderDetails
                .forEach(this::addOrderDetail);

    }

    public static Order createOrder(Long userId, List<OrderDetail> orderDetails) {
        return new Order(userId, orderDetails);
    }

    private String createOrderUid() {
        return UUID.randomUUID().toString();
    }

    //연관관계 메서드
    private void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public void successOrder() {
        this.status = COMPLETED;
    }

    public void cancelOrder() {
        this.status = CANCELED;
    }
}
