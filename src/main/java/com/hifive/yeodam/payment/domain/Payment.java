package com.hifive.yeodam.payment.domain;

import com.hifive.yeodam.order.domain.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.hifive.yeodam.payment.domain.PaymentStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private int price;

    @Enumerated(STRING)
    private PaymentStatus status;

    //결제 고유 번호
    private String paymentUid;

    @JoinColumn(name = "order_id")
    @OneToOne(fetch = LAZY)
    private Order order;


    private Payment(int price, Order order) {
        this.price = price;
        this.status = PENDING;
        setOrder(order);
    }

    public static Payment create(int price, Order order) {
        return new Payment(price, order);
    }

    private void setOrder(Order order) {
        this.order = order;
        order.setPayment(this);
    }

    public void successPayment(String paymentUid) {
        this.status = COMPLETED;
        this.paymentUid = paymentUid;
    }

    public void failPayment(String paymentUid) {
        this.status = FAILED;
        this.paymentUid = paymentUid;
    }
}
