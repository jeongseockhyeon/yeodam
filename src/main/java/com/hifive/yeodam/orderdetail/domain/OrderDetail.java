package com.hifive.yeodam.orderdetail.domain;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.order.domain.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public static OrderDetail create(Item item, int count, int price) {
        return new OrderDetail(item, count, price);
    }

    public int getTotalPrice() {
        return getPrice() * getCount();
    }

    private OrderDetail(Item item, int count, int price) {
        this.item = item;
        this.count = count;
        this.price = price;
    }
}
