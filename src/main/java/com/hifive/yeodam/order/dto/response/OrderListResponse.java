package com.hifive.yeodam.order.dto.response;

import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.payment.domain.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderListResponse {

    Slice<OrderListResponse.BeforeOrderResponse> beforeResponse;
    Slice<OrderListResponse.AfterOrderResponse> afterResponse;

    public OrderListResponse(Slice<BeforeOrderResponse> beforeResponse, Slice<AfterOrderResponse> afterResponse) {
        this.beforeResponse = beforeResponse;
        this.afterResponse = afterResponse;
    }

    @Data
    @NoArgsConstructor
    public static class BeforeOrderResponse {

        private String orderUid;
        private Long itemId;
        private String itemName;
        private String bookerName;
        private String phoneNumber;
        private String message;
        private String cardName;
        private int dDay;
        private LocalDateTime paymentAt;

        public BeforeOrderResponse(OrderDetail orderDetail) {
            this.orderUid = orderDetail.getOrder().getOrderUid();
            this.itemId = orderDetail.getItem().getId();
            this.itemName = orderDetail.getItem().getItemName();
            this.bookerName = orderDetail.getBookerName();
            this.phoneNumber = orderDetail.getBookerPhone();
            this.message = orderDetail.getMessage();
            this.cardName = orderDetail.getOrder().getPayment().getCardName();
            this.dDay = orderDetail.getReservation().getRemainingDay();
            this.paymentAt = orderDetail.getOrder().getPayment().getPaymentAt();
        }
    }

    @Data
    @NoArgsConstructor
    public static class AfterOrderResponse {

        private String itemName;
        private String orderUid;
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
}