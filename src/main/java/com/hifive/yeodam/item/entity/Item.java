package com.hifive.yeodam.item.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type")
@Entity
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private Seller seller
    private Long sellerId;

    private String itemName;

    private int price;

    private boolean reservation;

    public Item(Long sellerId, String itemName, int price, boolean reservation) {
        this.sellerId = sellerId;
        this.itemName = itemName;
        this.price = price;
        this.reservation = reservation;
    }

    public void updateItem(String itemName,int price) {

        this.itemName = itemName;
        this.price = price;
    }

    public abstract void updateSubItem(String... args);
}
