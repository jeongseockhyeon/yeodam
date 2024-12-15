package com.hifive.yeodam.item.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
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


    public Item(Long sellerId, String itemName) {
        this.sellerId = sellerId;
        this.itemName = itemName;
    }

    public void updateItem(String itemName) {
        this.itemName = itemName;
    }
}
