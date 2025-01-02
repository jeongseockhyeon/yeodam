package com.hifive.yeodam.cart.dto.query;

import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDto {
    private Long itemId;
    private String tourName;
    private String tourRegion;
    private String tourPeriod;
    private int tourPrice;
    private int maximum;
    private Long guideId;
    private String imgUrl;

    //주문 정보
    private int count;
    private String bookerName;
    private String phoneNumber;
    private String orderMessage;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public CartResponseDto(Cart cart, CartRequestDto requestDto) {
        this.itemId = requestDto.getItemId();
        this.tourName = requestDto.getTourName();
        this.tourRegion = requestDto.getTourRegion();
        this.tourPeriod = requestDto.getTourPeriod();
        this.tourPrice = cart.getTourPrice();
        this.maximum = requestDto.getMaximum();
        this.guideId = requestDto.getGuideId();
        this.imgUrl = requestDto.getImgUrl();
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
                .count(1)
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