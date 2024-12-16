package com.hifive.yeodam.orderdetail.domain;

import com.hifive.yeodam.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

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

    //TODO 추후 아이템으로 변경
    private Long item;
    private int count;
    private int price;

    public static OrderDetail create(Long item, int count, int price) {
        return new OrderDetail(item, count, price);
    }

    public int getTotalPrice() {
        return getPrice() * getCount();
    }

    private OrderDetail(Long item, int count, int price) {
        this.item = item;
        this.count = count;
        this.price = price;
    }
}
