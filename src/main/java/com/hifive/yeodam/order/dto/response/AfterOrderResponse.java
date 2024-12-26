package com.hifive.yeodam.order.dto.response;

import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.payment.domain.PaymentStatus;
import lombok.Data;

@Data
public class AfterOrderResponse {

    private String itemName;
    private String orderUid;

    //    private LocalDateTime paymentAt;
    private String orderStatus;

    public AfterOrderResponse(OrderDetail orderDetail) {
        this.itemName = orderDetail.getItem().getItemName();
        this.orderUid = orderDetail.getOrder().getOrderUid();
        this.orderStatus = getOrderStatus(orderDetail);
    }

    private String getOrderStatus(OrderDetail orderDetail) {
        if (isOrderStatusCompleted(orderDetail) && isPaymentStatusCompleted(orderDetail)) {
            return "이용완료";
        } else {
            return "취소";
        }
    }

    private boolean isPaymentStatusCompleted(OrderDetail orderDetail) {
        return orderDetail.getOrder().getPayment().getStatus().equals(PaymentStatus.COMPLETED);
    }

    private boolean isOrderStatusCompleted(OrderDetail orderDetail) {
        return orderDetail.getOrder().getStatus().equals(OrderStatus.COMPLETED);
    }

}
