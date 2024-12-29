package com.hifive.yeodam.inquiry.controller;

import com.hifive.yeodam.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquires/")
public class InquiryApiController {

    private InquiryService inquiryService;


}