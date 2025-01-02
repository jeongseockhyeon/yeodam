package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.dto.request.CancelOrderRequest;
import com.hifive.yeodam.order.dto.response.CreateOrderPaymentResponse;
import com.hifive.yeodam.order.facade.OrderFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderFacadeService orderFacadeService;

    @PatchMapping("/api/orders/{orderUid}")
    public ResponseEntity cancelOrder(@RequestBody CancelOrderRequest request) {
        orderFacadeService.cancelOrderPayment(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/orders")
    public CreateOrderPaymentResponse order(
            Principal principal,
            @Valid @ModelAttribute(name = "addOrderRequests") AddOrderRequest request) {

        return orderFacadeService.createOrderPayment(request, principal);
    }
}
