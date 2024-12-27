package com.hifive.yeodam.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.hifive.yeodam.global.constant.PaymentConst.ORDER_UID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    @GetMapping("/success")
    public String successForm(@RequestParam(name = ORDER_UID) String orderUid, Model model) {
        model.addAttribute("orderUid", orderUid);
        return "/payment/success-form";
    }

    @GetMapping("/fail")
    public String failPayment(@RequestParam(name = ORDER_UID) String orderUid) {
        return "/payment/fail-form";
    }
}
