package com.hifive.yeodam.cart.dto.query;

import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.item.dto.ItemImgResDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.entity.Tour;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDto {
    private Long cartId;
    private Long itemId;
    private String itemName;
    private int price;
    private int count;
    private boolean reservation;
    private boolean countModifiable; //수량 변경 가능 여부
    private List<ItemImgResDto> images;
    private TourItemResDto tourItem;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    private CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.itemId = cart.getItem().getId();
        this.itemName = cart.getItem().getItemName();
        this.price = cart.getPrice();
        this.count = cart.getCount();
        this.reservation = cart.getItem().isReservation();
        this.countModifiable = !cart.getItem().isReservation(); //일반 상품
        this.images = cart.getItem().getItemImages().stream()
                .map(ItemImgResDto::new)
                .collect(Collectors.toList());

        //Tour 상품 추가 정보
        if (cart.getItem() instanceof Tour tour){
            this.tourItem = new TourItemResDto(tour);
            this.startDate = cart.getStartDate();
            this.endDate = cart.getEndDate();
        }
    }
}
