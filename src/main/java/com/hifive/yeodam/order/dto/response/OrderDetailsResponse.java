package com.hifive.yeodam.order.dto.response;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.reservation.entity.Reservation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

import static com.hifive.yeodam.orderdetail.domain.OrderDetailStatus.USED;

@Data
@NoArgsConstructor
public class OrderDetailsResponse {

    Slice<OrderDetailsResponse.BeforeOrderResponse> beforeResponse;
    Slice<OrderDetailsResponse.AfterOrderResponse> afterResponse;

    public OrderDetailsResponse(Slice<BeforeOrderResponse> beforeResponse, Slice<AfterOrderResponse> afterResponse) {
        this.beforeResponse = beforeResponse;
        this.afterResponse = afterResponse;
    }

    private static String getStorePath(Item item) {

        if (item.getItemImages().isEmpty()) {
            return null;
        }
        return item.getItemImages().getFirst().getStorePath();
    }

    @Data
    @NoArgsConstructor
    public static class BeforeOrderResponse {

        private Long itemId;
        private String itemName;
        private String imgPath;
        private int price;
        private String orderUid;
        private String bookerName;
        private String phoneNumber;
        private String message;
        private int dDay;
        private String guideName;
        private String cardName;
        private LocalDateTime paymentAt;

        public BeforeOrderResponse(OrderDetail orderDetail) {

            Item item = orderDetail.getItem();
            Order order = orderDetail.getOrder();
            Payment payment = order.getPayment();
            Reservation reservation = orderDetail.getReservation();

            this.itemId = item.getId();
            this.itemName = item.getItemName();
            this.imgPath = getStorePath(item);
            this.price = order.getTotalPrice();
            this.orderUid = order.getOrderUid();
            this.bookerName = orderDetail.getBookerName();
            this.phoneNumber = orderDetail.getBookerPhone();
            this.cardName = payment.getCardName();
            this.paymentAt = payment.getPaymentAt();
            this.message = orderDetail.getMessage();
            this.dDay = reservation.getRemainingDay();
            this.guideName = reservation.getGuide().getName();
        }

    }

    @Data
    @NoArgsConstructor
    public static class AfterOrderResponse {

        private Long itemId;

        private String itemName;
        private String imgPath;
        private String orderUid;
        private boolean orderStatus; // 사용완료 했으면 true, 취소면 false
        private boolean reviewStatus; // 작성안했으면 true, 작성했으면 false

        public AfterOrderResponse(OrderDetail orderDetail) {

            Item item = orderDetail.getItem();

            this.itemName = item.getItemName();
            this.imgPath = getStorePath(orderDetail.getItem());
            this.itemId = item.getId();
            this.orderUid = orderDetail.getOrder().getOrderUid();
            this.orderStatus = getOrderStatus(orderDetail);
            this.reviewStatus = isReviewWritten(orderDetail);
        }

        private boolean getOrderStatus(OrderDetail orderDetail) {
            return orderDetail.getStatus().equals(USED);
        }

        private boolean isReviewWritten(OrderDetail orderDetail) {
            return orderDetail.getReview() == null;
        }
    }
}