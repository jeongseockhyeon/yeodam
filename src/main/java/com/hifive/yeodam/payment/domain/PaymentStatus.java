package com.hifive.yeodam.payment.domain;

public enum PaymentStatus {
    PENDING, // 결제 전
    COMPLETED, // 결제 성공
    FAILED, // 결제 실패
    CANCELED, // 결제 전부 취소된 상태
    PARTIALLY_CANCELED, //결제 부분 취소된 상태
}
