package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
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

    @ResponseBody
    @PostMapping
    public ResponseEntity order(/*Principal principal,*/ AddOrderRequest request, RedirectAttributes redirectAttributes) {

        Principal principal = new TestPrincipal("123@a.com");

        String orderUid = orderService.order(request, principal);
        return ResponseEntity.ok(Map.of("orderUid", orderUid));
    }

    //TODO 추후 삭제 예정
    @AllArgsConstructor
    static class TestPrincipal implements Principal {

        private String name;

        @Override
        public String getName() {
            return this.name;
        }
    }
}
