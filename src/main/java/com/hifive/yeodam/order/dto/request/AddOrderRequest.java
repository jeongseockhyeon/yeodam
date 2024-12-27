package com.hifive.yeodam.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequest {

    public List<orderRequest> orderRequests;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class orderRequest {
        private Long itemId;
        private String name;
        private int count;
        private int price;
        private String bookerName;   // 예약자 이름
        private String phoneNumber;  // 예약자 연락처
        private String orderMessage; // 예약 메시지

        private Long guideId;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
