package com.hifive.yeodam.wish.dto;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.wish.entity.Wish;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WishDto {
    private Long itemId;
    private String itemName;
    private String description;
    private int price; //일반상품
    private int minPrice; //예약상품 최저가
    private boolean wished;
    private boolean reservation;

    public WishDto(Long itemId, String itemName, String description, int price, int minPrice, boolean wished, boolean reservation) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.minPrice = minPrice;
        this.wished = wished;
        this.reservation = reservation;
    }

    // 찜 목록 페이지용
    public static WishDto from(Wish wish) {
        Item item = wish.getItem();
        return new WishDto(
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                item.isReservation() ? null : item.getPrice(), //일반상품
                item.isReservation() ? item.getPrice() : null, //예약상품
                true,
                item.isReservation()
        );
    }

}
