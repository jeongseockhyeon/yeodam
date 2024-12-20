package com.hifive.yeodam.payment.controller;

import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.dto.PaymentResponse;
import com.hifive.yeodam.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> paymentForm(@RequestParam("orderUid") String orderUid) {
        Long paymentId = paymentService.createPayment(orderUid);
        PaymentResponse paymentResponse = paymentService.findRequestPayment(paymentId); //커멘드 쿼리 분리
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping("/success")
    public ResponseEntity validationPayment(@RequestBody PaymentRequestCallBack request) {
        String orderUid = paymentService.validatePayment(request);
        return ResponseEntity.ok(Map.of("orderUid", orderUid));
    }

    @PostMapping("/cancel")
    public String cancelPayment(@RequestParam("orderUid") String orderUid) {
        paymentService.cancel(orderUid);
        return "ok";
    }
}