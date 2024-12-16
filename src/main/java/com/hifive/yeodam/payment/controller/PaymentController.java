package com.hifive.yeodam.payment.controller;

import com.hifive.yeodam.payment.dto.PaymentRequest;
import com.hifive.yeodam.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment")
    public String paymentForm(@RequestParam("orderUid") String orderUid, Model model) {
        Long paymentId = paymentService.createPayment(orderUid);
        model.addAttribute("paymentRequest", paymentService.findRequestPayment(paymentId));
        return "payment/paymentForm";
    }
}
