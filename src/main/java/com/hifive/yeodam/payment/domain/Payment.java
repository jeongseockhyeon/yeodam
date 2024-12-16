package com.hifive.yeodam.payment.domain;

import com.hifive.yeodam.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.hifive.yeodam.payment.domain.PaymentStatus.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private int price;

    @Enumerated
    private PaymentStatus status;

    //결제 고유 번호
    private String paymentUid;

    @JoinColumn(name = "order_id")
    @OneToOne(fetch = LAZY)
    private Order order;


    private Payment(int price, Order order) {
        this.price = price;
        this.status = PENDING;
        this.order = order;
    }

    public static Payment create(int price, Order order) {
        return new Payment(price, order);
    }

    public void changePaymentBySuccess(PaymentStatus status, String paymentUid) {
        this.status = status;
        this.paymentUid = paymentUid;
    }
}
