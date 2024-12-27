package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.dto.request.CancelOrderRequest;
import com.hifive.yeodam.order.dto.response.CancelOrderResponse;
import com.hifive.yeodam.order.dto.response.CreateOrderResponse;
import com.hifive.yeodam.order.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderCommandService orderCommandService;

    @PatchMapping("/api/orders/{orderUid}")
    public CancelOrderResponse cancelOrder(@RequestBody CancelOrderRequest request) {
        return orderCommandService.cancelOrder(request);
    }

    @PostMapping("/api/orders")
    public CreateOrderResponse order(Principal principal, @ModelAttribute(name = "addOrderRequests") AddOrderRequest request) {
        return orderCommandService.order(request, principal);
    }
}
