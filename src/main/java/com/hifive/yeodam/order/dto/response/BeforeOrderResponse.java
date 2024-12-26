package com.hifive.yeodam.order.dto.response;

import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BeforeOrderResponse {

    private String orderUid;
    private Long itemId;
    private String itemName;
    private String bookerName;
    private String phoneNumber;
    private String message;
    private String cardName;

    public BeforeOrderResponse(OrderDetail orderDetail) {
        this.orderUid = orderDetail.getOrder().getOrderUid();
        this.itemId = orderDetail.getItem().getId();
        this.itemName = orderDetail.getItem().getItemName();
        this.bookerName = orderDetail.getBookerName();
        this.phoneNumber = orderDetail.getBookerPhone();
        this.message = orderDetail.getMessage();
        this.cardName = orderDetail.getOrder().getPayment().getCardName();
    }
}
