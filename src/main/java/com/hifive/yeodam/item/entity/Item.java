package com.hifive.yeodam.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.review.domain.Review;
import com.hifive.yeodam.seller.entity.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
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

    @Column(columnDefinition = "TEXT")
    private String description;

    private int price;

    private int stock;

    private double rate;

    private boolean active;

    private boolean reservation;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemImage> itemImages = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Item(Seller seller,
                String itemName,
                String description,
                int price,
                boolean reservation,
                int stock,
                double rate,
                boolean active) {
        this.seller = seller;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.reservation = reservation;
        this.stock = stock;
        this.rate = rate;
        this.active = active;
    }

    public void updateItem(String itemName,String description,int price) {

        this.itemName = itemName;
        this.description = description;
        this.price = price;
    }

    public void updateActive(boolean active) {
        this.active = active;
    }

    public void addStock() {
        this.stock += 1;
    }

    public void removeStock() {
        this.stock -= 1;
    }

    public void updateRate(double rate, int totalCount) {
        int newTotalCount = totalCount + 1;
        double newAverage = (this.rate * totalCount + rate) / newTotalCount;
        this.rate = Math.round(newAverage * 10) / 10.0;
    }

    public abstract void updateSubItem(String region,String period,Integer maximum);

    public void changeSeller(Seller seller) {
        this.seller = seller;
    }

}
