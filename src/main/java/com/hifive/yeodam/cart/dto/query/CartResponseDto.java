package com.hifive.yeodam.cart.dto.query;

import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.tour.entity.Tour;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDto {
    private Long cartId;
    private Long itemId;
    private String tourName;
    private String tourRegion;
    private String tourPeriod;
    private int tourPrice;
    private int maximum;
    private Long guideId;
    private String guideName;
    private String imgUrl;
    private boolean reservation;

    private int count;
    private String bookerName;
    private String phoneNumber;
    private String orderMessage;
    private LocalDate startDate;
    private LocalDate endDate;

    public CartResponseDto(Cart cart, CartRequestDto requestDto) {
        if (cart != null) {
            this.cartId = cart.getId();
            Item item = cart.getItem();
            this.itemId = item.getId();
            this.tourName = item.getItemName();
            this.tourPrice = cart.getTourPrice();

            if (item instanceof Tour) {
                Tour tour = (Tour) item;
                this.tourRegion = tour.getRegion();
                this.tourPeriod = tour.getPeriod();
                this.maximum = tour.getMaximum();
                this.reservation = true;
                this.startDate = cart.getStartDate();
                this.endDate = cart.getEndDate();
            }

            if (requestDto != null && requestDto.getGuideId() != null) {
                this.guideId = requestDto.getGuideId();
                this.guideName = requestDto.getGuideName();
            } else if (cart.getGuide() != null) {
                this.guideId = cart.getGuide().getGuideId();
                this.guideName = cart.getGuide().getName();
            }

            if (requestDto != null && requestDto.getImgUrl() != null) {
                this.imgUrl = requestDto.getImgUrl();
            } else {
                this.imgUrl = cart.getItem().getItemImages().stream()
                        .filter(ItemImage::isThumbnail)
                        .findFirst()
                        .map(ItemImage::getStorePath)
                        .orElse(null);
            }

            this.count = cart.getCount();
        }
    }

    // 빌더 패턴 정적 내부 클래스 추가
    public static CartResponseDtoBuilder builder() {
        return new CartResponseDtoBuilder();
    }

    public static class CartResponseDtoBuilder {
        private Cart cart;
        private CartRequestDto requestDto;

        CartResponseDtoBuilder() {}

        public CartResponseDtoBuilder cart(Cart cart) {
            this.cart = cart;
            return this;
        }

        public CartResponseDtoBuilder requestDto(CartRequestDto requestDto) {
            this.requestDto = requestDto;
            return this;
        }

        public CartResponseDto build() {
            return new CartResponseDto(cart, requestDto);
        }
    }

    public AddOrderRequest.orderRequest toOrderRequest() {
        return AddOrderRequest.orderRequest.builder()
                .itemId(this.itemId)
                .name(this.tourName)
                .price(this.tourPrice)
                .guideId(this.guideId)
                .bookerName(this.bookerName)
                .phoneNumber(this.phoneNumber)
                .orderMessage(this.orderMessage)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .count(this.maximum) //상품 최대 인원수
                .build();
    }

    // 주문 정보 설정을 위한 메서드
    public void setOrderInfo(String bookerName, String phoneNumber,
                             String orderMessage, LocalDate startDate,
                             LocalDate endDate) {
        this.bookerName = bookerName;
        this.phoneNumber = phoneNumber;
        this.orderMessage = orderMessage;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
