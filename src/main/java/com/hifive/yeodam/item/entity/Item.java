package com.hifive.yeodam.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hifive.yeodam.seller.entity.Seller;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Seller seller;

    private String itemName;

    private int price;

    private int stock;

    private boolean reservation;

    public Item(Seller seller, String itemName, int price, boolean reservation, int stock) {
        this.seller = seller;
        this.itemName = itemName;
        this.price = price;
        this.reservation = reservation;
        this.stock = stock;
    }

    public void updateItem(String itemName,int price) {

        this.itemName = itemName;
        this.price = price;
    }

    public void addStock() {
        this.stock += 1;
    }

    public void removeStock() {
        this.stock -= 1;
    }

    public abstract void updateSubItem(String... args);
}
