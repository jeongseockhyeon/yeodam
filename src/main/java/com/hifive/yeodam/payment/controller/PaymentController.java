package com.hifive.yeodam.payment.controller;

import com.hifive.yeodam.payment.dto.PaymentRequest;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public String paymentForm(@RequestParam("orderUid") String orderUid, Model model) {
        Long paymentId = paymentService.createPayment(orderUid);
        PaymentRequest requestPayment = paymentService.findRequestPayment(paymentId); //커멘드 쿼리 분리
        model.addAttribute("paymentRequest", requestPayment);
        return "payment/paymentForm";
    }

    @PostMapping("/success")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentRequestCallBack request) {
        IamportResponse<com.siot.IamportRestClient.response.Payment> response = paymentService.paymentCallBack(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/fail")
    public String PaymentFail(@RequestBody PaymentRequestCallBack request) {
        paymentService.paymentFail(request);
        return "실패함";
    }
}
