package com.hifive.yeodam.order.domain;

import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hifive.yeodam.order.domain.OrderStatus.CANCEL;
import static com.hifive.yeodam.order.domain.OrderStatus.ORDER;
import static jakarta.persistence.CascadeType.PERSIST;
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

    @OneToMany(mappedBy = "order", cascade = PERSIST)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private Order(Long userId, List<OrderDetail> orderDetails) {
        this.userId = userId;
        this.status = ORDER;
        orderDetails
                .forEach(this::addOrderDetail);

    }

    public static Order createOrder(Long userId, List<OrderDetail> orderDetails) {
        return new Order(userId, orderDetails);
    }

    //연관관계 메서드
    private void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }
    public void cancelOrder() {
        this.status = CANCEL;
    }


}
