package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.service.OrderCommandService;
import com.hifive.yeodam.order.service.OrderQueryService;
import com.hifive.yeodam.orderdetail.service.OrderDetailQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderFormController {

    private final OrderQueryService orderQueryService;
    private final OrderDetailQueryService orderDetailQueryService;

    @GetMapping("/order")
    public String orderForm(Model model) {
        LocalDate start = LocalDate.of(2024, 12, 31);
        LocalDate end = LocalDate.of(2025, 1, 3);
        AddOrderRequest.orderRequest orderRequests = new AddOrderRequest.orderRequest(1L, "제주도 푸른밤11", 2, 100, "길동이", "1234-1234", null, 1L,start, end);
        AddOrderRequest.orderRequest orderRequests2 = new AddOrderRequest.orderRequest(2L, "제주도 푸른밤22", 2, 200, "길동이", "1234-1234", null,2L, start,end);
        AddOrderRequest addOrderRequest = new AddOrderRequest(List.of(orderRequests/*, orderRequests2*/));
        model.addAttribute("addOrderRequest", addOrderRequest);

        return "order/order-form";
    }

    @GetMapping("/orders")
    public String orderList(
            @RequestParam(name = "beforeLimit", defaultValue = "5") int beforeLimit,
            @RequestParam(name = "afterLimit", defaultValue = "5") int afterLimit,
            Principal principal, Model model) {

        model.addAttribute("orderDetailsResponse", orderDetailQueryService
                .findOrderDetails(beforeLimit, afterLimit, principal));

        return "order/order-list";
    }

    @GetMapping("/orders/{orderUid}/continue")
    public String retryOrder(@PathVariable String orderUid, Model model) {
        AddOrderRequest addOrderRequest = orderQueryService.changeToAddOrderRequest(orderUid);
        model.addAttribute("addOrderRequest", addOrderRequest);
        return "order/order-form";
    }
}
