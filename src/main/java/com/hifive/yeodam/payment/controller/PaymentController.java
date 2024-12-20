package com.hifive.yeodam.payment.controller;

import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/success")
    public String successForm(@RequestParam(name = "orderUid") String orderUid, Model model) {
        model.addAttribute("orderUid", orderUid);
        return "payment/successForm";
    }

    @PostMapping("/fail")
    public String failPayment(@RequestBody PaymentRequestCallBack request) {
        paymentService.paymentFail(request);
        return "실패함";
    }
}
