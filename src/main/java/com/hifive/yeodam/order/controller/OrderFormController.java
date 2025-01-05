package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.item.service.ItemService;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderFormController {

    private final OrderQueryService orderQueryService;
    private final OrderDetailQueryService orderDetailQueryService;
    private final ItemService itemService;

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

    @GetMapping("/sellers/items/orders")
    public String getOrderBySeller(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            Principal principal, Model model) {

        model.addAttribute("response", itemService.findItemsWithOrderCountBySeller(principal, offset, limit));
        return "/order/order-seller-list";
    }

    @GetMapping("/sellers/items/{itemId}/orders")
    public String getOrderDetailBySeller(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            Principal principal, Model model) {

        model.addAttribute("response", orderDetailQueryService.findOrderDetailsBySeller(principal, itemId, offset, limit));

        return "/order/order-seller-detail";
    }
}
