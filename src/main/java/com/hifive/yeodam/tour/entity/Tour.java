package com.hifive.yeodam.tour.entity;

import com.hifive.yeodam.item.entity.Item;

import com.hifive.yeodam.seller.entity.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DiscriminatorValue("t")
public class Tour extends Item {

    private String region;

    private String period;

    private Integer maximum;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourCategory> tourCategories = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourGuide> tourGuides  = new ArrayList<>();


    @Builder
    public Tour(Seller seller,
                String itemName,
                String region,
                String period,
                String description,
                int price,
                boolean reservation,
                Integer maximum,
                int stock,
                double rate,
                boolean active
                )
    {
        super(seller,itemName,description,price,reservation,stock,rate,active);
        this.region = region;
        this.period = period;
        this.maximum = maximum;
    }

    @Override
    public void updateSubItem(String region, String period, Integer maximum) {
        this.region = region;
        this.period = period;
        this.maximum = maximum;
    }
}
