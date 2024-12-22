package com.hifive.yeodam.order.domain;

import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hifive.yeodam.order.domain.OrderStatus.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
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

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User User;

    @Enumerated(STRING)
    private OrderStatus status;

    private String bookerName;
    private String bookerPhone;
    private String message;

    private String orderUid; //주문번호

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "order", fetch = LAZY)
    private Payment payment;

    private Order(User user, String bookerName, String bookerPhone,
                  String message, List<OrderDetail> orderDetails) {
        this.User = user;
        this.status = PENDING;
        this.bookerName = bookerName;
        this.bookerPhone = bookerPhone;
        this.message = message;
        this.orderUid = createOrderUid();
        setOrderDetails(orderDetails);
    }

    public static Order createOrder(User user, String bookerName, String bookerPhone,
                                    String message, List<OrderDetail> orderDetails) {
        return new Order(user, bookerName, bookerPhone, message, orderDetails);
    }

    private String createOrderUid() {
        return UUID.randomUUID().toString();
    }

    private void setOrderDetails(List<OrderDetail> orderDetails) {
        orderDetails
                .forEach(this::addOrderDetail);
    }

    //== 연관관계 메서드 ==//
    private void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    //== bizLogic ==//
    public int getTotalPrice() {
        return orderDetails.stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();
    }

    public String getItemSummary() {

        StringBuilder sb = new StringBuilder();
        int size = orderDetails.size();

        if (size > 1) {
            sb.append(orderDetails.getFirst().getItem().getItemName())
                    .append(" 외 ")
                    .append(size)
                    .append("건");

            return sb.toString();
        }

        return sb.append(orderDetails.getFirst().getItem().getItemName())
                .toString();
    }


    public void successOrder() {
        this.status = COMPLETED;
    }

    public void failOrder() {
        this.status = FAILED;
    }

    public void cancelOrder() {
        this.status = CANCELED;
    }
}
