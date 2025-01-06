package com.hifive.yeodam.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    @GetMapping("/{orderUid}/success")
    public String successForm(@PathVariable String orderUid, Model model) {
        model.addAttribute("orderUid", orderUid);
        return "payment/success-form";
    }

    @GetMapping("/{orderUid}/fail")
    public String failPayment() {
        return "payment/fail-form";
    }
}
