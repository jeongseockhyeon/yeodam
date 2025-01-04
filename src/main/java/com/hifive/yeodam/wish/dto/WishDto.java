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

    public WishDto(Long itemId, String itemName, String description, int price, boolean wished, boolean reservation, String imgUrl) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.wished = wished;
        this.reservation = reservation;
        this.imgUrl = imgUrl;
    }

    // 찜 목록 페이지용
    public static WishDto from(Wish wish) {
        Item item = wish.getItem();

        // 이미지 URL 가져오기
        String imageUrl = null;
        if (!item.getItemImages().isEmpty()) {
            imageUrl = item.getItemImages().stream()
                    .filter(ItemImage::isThumbnail)
                    .findFirst()
                    .map(ItemImage::getStorePath)
                    .orElseGet(() -> item.getItemImages().get(0).getStorePath());
        }

        return new WishDto(
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                item.getPrice(),
                true,
                item.isReservation(),
                imageUrl
        );
    }
}