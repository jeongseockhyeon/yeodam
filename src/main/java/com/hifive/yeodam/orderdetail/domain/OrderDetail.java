package com.hifive.yeodam.orderdetail.domain;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.review.domain.Review;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.hifive.yeodam.orderdetail.domain.OrderDetailStatus.PENDING;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @Setter
    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = LAZY)
    private Order order;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = LAZY)
    private Item item;

    private int count;
    private int price;
    private String bookerName;
    private String bookerPhone;
    private String message;

    @Enumerated(STRING)
    private OrderDetailStatus status;

    @JoinColumn(name = "reservation_id")
    @OneToOne(fetch = LAZY)
    private Reservation reservation;

    @Setter
    @OneToOne(mappedBy = "orderDetail")
    private Review review;

    @Builder
    public OrderDetail(Item item, int count, int price, String bookerName, String bookerPhone, String message, Reservation reservation) {
        setItem(item);
        this.count = count;
        this.price = price;
        this.bookerName = bookerName;
        this.bookerPhone = bookerPhone;
        this.message = message;
        this.status = PENDING;
        this.reservation = reservation;
    }

    private void setItem(Item item) {
        this.item = item;
        item.getOrderDetails().add(this);
    }

    public static OrderDetail create(Item item, int count, int price, String bookerName, String bookerPhone, String message, Reservation reservation) {
        return new OrderDetail(item, count, price, bookerName, bookerPhone, message, reservation);
    }

    public int getTotalPrice() {
        return getPrice() * getCount();
    }

    public void changeStatus(OrderDetailStatus status) {
        this.status = status;
    }
}
