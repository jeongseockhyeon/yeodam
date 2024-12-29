package com.hifive.yeodam.payment.domain;

import com.hifive.yeodam.order.domain.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.hifive.yeodam.global.constant.PaymentConst.PAYMENT_BEFORE_CARD_NAME;
import static com.hifive.yeodam.global.constant.PaymentConst.PAYMENT_UID_BEFORE_PAYMENT;
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

    private String cardName;

    @JoinColumn(name = "order_id")
    @OneToOne(fetch = LAZY)
    private Order order;

    private LocalDateTime paymentAt;
    private LocalDateTime cancellationAt;


    private Payment(int price, Order order) {
        this.price = price;
        this.status = PENDING;
        this.paymentUid = PAYMENT_UID_BEFORE_PAYMENT;
        this.cardName = PAYMENT_BEFORE_CARD_NAME;
        this.paymentAt = LocalDateTime.of(2000, 1, 1, 00, 00);
        this.cancellationAt = LocalDateTime.of(2000, 1, 1, 00, 00);
        setOrder(order);
    }

    public static Payment create(int price, Order order) {
        return new Payment(price, order);
    }

    private void setOrder(Order order) {
        this.order = order;
        order.setPayment(this);
    }

    public void successPayment(String paymentUid, String cardName) {
        this.status = COMPLETED;
        this.paymentUid = paymentUid;
        this.cardName = cardName;
        this.paymentAt = LocalDateTime.now();
    }

    public void paymentFail(String paymentUid) {
        this.status = FAILED;
        this.paymentUid = paymentUid;
        this.cancellationAt = LocalDateTime.now();
    }

    public void cancel() {
        status = CANCELED;
    }
}
