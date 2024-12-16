package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public String orderForm() {
        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(AddOrderRequest request, RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("orderUid", orderService.order(request));
        return "redirect:/payment?orderUid={orderUid}";

    }
}
