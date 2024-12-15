package com.hifive.yeodam.tour.entity;

import com.hifive.yeodam.item.entity.Item;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Tour extends Item {

/*    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;*/

    private String region;

    private String period;

    private String description;

    private int price;

/*    @OneToOne
    private Item item;*/

    @Builder
    public Tour(Long sellerId, String itemName,String region, String period, String description, int price){
        super(sellerId,itemName);
        this.region = region;
        this.period = period;
        this.description = description;
        this.price = price;
    }

    public void updateTour(String region, String period, String description, int price) {
        this.region = region;
        this.period = period;
        this.description = description;
        this.price = price;
    }
}
