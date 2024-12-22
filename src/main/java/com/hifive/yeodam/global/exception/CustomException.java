package com.hifive.yeodam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
   CustomErrorCode customErrorCode;
}
