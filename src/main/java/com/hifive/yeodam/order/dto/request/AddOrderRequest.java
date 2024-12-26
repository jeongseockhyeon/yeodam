package com.hifive.yeodam.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequest {

    public List<orderRequest> orderRequests;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class orderRequest {
        private Long itemId;
        private String name;
        private int count;
        //        private String period;
        private int price;
        private String bookerName;   // 예약자 이름
        private String phoneNumber;  // 예약자 연락처
        private String orderMessage; // 예약 메시지
    }
}
