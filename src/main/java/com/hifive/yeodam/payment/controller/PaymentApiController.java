package com.hifive.yeodam.payment.controller;

import com.hifive.yeodam.payment.dto.CancelPaymentRequest;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.dto.PaymentResponse;
import com.hifive.yeodam.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.hifive.yeodam.global.constant.PaymentConst.ORDER_UID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestParam(ORDER_UID) String orderUid) {
        Long paymentId = paymentService.createPayment(orderUid);
        PaymentResponse paymentResponse = paymentService.findRequestPayment(paymentId); //커멘드 쿼리 분리
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping("/fail")
    public ResponseEntity failPayment(@RequestBody PaymentRequestCallBack request) {
        paymentService.paymentFail(request);
        return ResponseEntity.ok(Map.of(ORDER_UID, request.getOrderUid()));
    }

    //브라우저 종료시 결제 상태 확인
    @PostMapping("/check")
    public void checkPaymentStatus(@RequestParam(name = ORDER_UID) String orderUid) {
        paymentService.checkPaymentStatus(orderUid);
    }

    @PostMapping("/validate")
    public ResponseEntity validationPayment(@RequestBody PaymentRequestCallBack request) {
        String orderUid = paymentService.validatePayment(request);
        return ResponseEntity.ok(Map.of(ORDER_UID, orderUid));
    }

    @PostMapping("/cancel")
    public ResponseEntity cancelPayment(@RequestBody CancelPaymentRequest request) {
        paymentService.cancel(request);
        return ResponseEntity.noContent().build();
    }
}