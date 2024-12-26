package com.hifive.yeodam.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {

    Slice<BeforeOrderResponse> beforeResponse;
    Slice<AfterOrderResponse> afterResponse;
}
