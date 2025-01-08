package com.hifive.yeodam.order.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class OrderTestController {
    private final UserService userService;

    /*임시 요청 경로*/
    @GetMapping("/order-test")
    public String order(@RequestParam("itemId") String itemId,
                        @RequestParam("name") String tourName,
                        @RequestParam("count") String count,
                        @RequestParam("guideId") String guideId,
                        @RequestParam("startDate") String startDate,
                        @RequestParam("endDate") String endDate,
                        @RequestParam("tourPrice") String tourPrice,
                        Model model,
                        @AuthenticationPrincipal Auth auth) {
        UserResponse user = userService.getUserByAuth(auth);
        String bookerName = user.getName();
        String phoneNumber = user.getPhone();
        String orderMessage = null;
        AddOrderRequest.orderRequest orderRequests = new AddOrderRequest.orderRequest(
                Long.valueOf(itemId),
                tourName,
                Integer.parseInt(count),
                Integer.parseInt(tourPrice),
                bookerName,
                phoneNumber,
                orderMessage,
                Long.valueOf(guideId),
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
        AddOrderRequest addOrderRequest = new AddOrderRequest(List.of(orderRequests));
        model.addAttribute("addOrderRequest", addOrderRequest);
        return "order/order-form";
    }
}
