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



/*    @OneToOne
    private Item item;*/

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
