package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public String testOrderForm(Model model) {

        AddOrderRequest.ItemRequest itemRequest = new AddOrderRequest.ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setName("제주도 푸른밤");
        itemRequest.setPrice(100);
        itemRequest.setCount(2);
        List<AddOrderRequest.ItemRequest> items = List.of(itemRequest);

        model.addAttribute("orderRequest",
                new AddOrderRequest("길동이", "1234-1234", null, items));

        return "order/order-form";
    }

    @PostMapping("/order")
    public String order(/*Principal principal,*/ AddOrderRequest request, RedirectAttributes redirectAttributes) {

        Principal principal = new TestPrincipal("123@a.com");

        redirectAttributes.addAttribute("orderUid", orderService.order(request, principal));
        return "redirect:/payment?orderUid={orderUid}";
    }

    static class TestPrincipal implements Principal {

        private String name;

        @Override
        public String getName() {
            return this.name;
        }

        public TestPrincipal(String name) {
            this.name = name;
        }
    }
}
