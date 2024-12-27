package com.hifive.yeodam.payment.controller;

import com.hifive.yeodam.payment.dto.CancelPaymentRequest;
import com.hifive.yeodam.payment.dto.CreatePaymentResponse;
import com.hifive.yeodam.payment.dto.PaymentOrderUidResponse;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hifive.yeodam.global.constant.PaymentConst.ORDER_UID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping
    public CreatePaymentResponse createPayment(@RequestParam(ORDER_UID) String orderUid) {
        Long paymentId = paymentService.createPayment(orderUid);
        return paymentService.findRequestPayment(paymentId);
    }

    @PostMapping("/fail")
    public PaymentOrderUidResponse failPayment(@RequestBody PaymentRequestCallBack request) {
        paymentService.paymentFail(request);
        return PaymentOrderUidResponse.builder()
                .orderUid(request.getOrderUid())
                .build();
    }

    //브라우저 종료시 결제 상태 확인
    @PostMapping("/check")
    public void checkPaymentStatus(@RequestParam(name = ORDER_UID) String orderUid) {
        paymentService.checkPaymentStatus(orderUid);
    }

    @PostMapping("/validate")
    public PaymentOrderUidResponse validationPayment(@RequestBody PaymentRequestCallBack request) {
        return paymentService.validatePayment(request);
    }

    @PostMapping("/cancel")
    public ResponseEntity cancelPayment(@RequestBody CancelPaymentRequest request) {
        paymentService.cancel(request);
        return ResponseEntity.noContent().build();
    }
}