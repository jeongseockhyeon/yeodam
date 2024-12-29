package com.hifive.yeodam.item.dto;

import com.hifive.yeodam.item.entity.ItemImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemImgResDto {
    private final Long id;
    private final String imgName;
    private final String imgUrl;

    public ItemImgResDto(ItemImage itemImage) {
        this.id = itemImage.getId();
        this.imgName = itemImage.getOriginalName();
        this.imgUrl = itemImage.getStorePath();
    }
}
