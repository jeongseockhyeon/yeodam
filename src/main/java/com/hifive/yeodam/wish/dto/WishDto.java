package com.hifive.yeodam.wish.dto;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.wish.entity.Wish;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WishDto {
    private Long itemId;
    private String itemName;
    private String description;
    private int price;
    private boolean wished;
    private boolean reservation;
    private String imgUrl;

    public WishDto(Long itemId, String itemName, String description, int price, boolean wished, boolean reservation) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.wished = wished;
        this.reservation = reservation;
    }

    // 찜 목록 페이지용
    public static WishDto from(Wish wish) {
        Item item = wish.getItem();
        WishDto wishDto = new WishDto(
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                item.getPrice(),
                true,
                item.isReservation()
        );

        // 썸네일 이미지 URL 설정
        wishDto.imgUrl = item.getItemImages().stream()
                .filter(ItemImage::isThumbnail)
                .findFirst()
                .map(ItemImage::getStorePath)
                .orElse(null);

        return wishDto;
    }
}
