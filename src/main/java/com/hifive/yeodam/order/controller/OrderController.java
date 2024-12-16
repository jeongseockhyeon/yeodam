package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public String orderForm() {
        return "order/orderForm";
    }

    @GetMapping("/orders")
    public String orderList(Model model) {
        model.addAttribute("orders", orderService.findOrders());
        return "order/orderList";
    }

    @PostMapping("/order")
    public String order(AddOrderRequest request) {
        orderService.order(request);
        return "redirect:/order/orderList";
    }
}
