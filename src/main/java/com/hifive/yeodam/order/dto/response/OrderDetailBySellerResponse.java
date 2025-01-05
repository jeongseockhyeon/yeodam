package com.hifive.yeodam.order.dto.response;

import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class OrderDetailBySellerResponse {

    private String orderUid;
    private Long itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private OrderDetailStatus status;
}
