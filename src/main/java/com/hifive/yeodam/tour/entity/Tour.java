package com.hifive.yeodam.tour.entity;

import com.hifive.yeodam.item.entity.Item;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class Tour extends Item {

    private String region;

    private String period;

    private String description;

    @OneToMany(mappedBy = "tour",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TourCategory> tourCategories = new ArrayList<>();


    @Builder
    public Tour(Long sellerId, String itemName,String region, String period, String description, int price){
        super(sellerId,itemName,price);
        this.region = region;
        this.period = period;
        this.description = description;
    }


    @Override
    public void updateSubItem(String... args) {
        this.region = args[0];
        this.period = args[1];
        this.description = args[2];
    }
}
