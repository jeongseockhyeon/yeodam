package com.hifive.yeodam.inquiry.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryViewController {
    @GetMapping("/user")
    public String showUserInquiryList() {
        return "inquiry/user-inquiry";
    }
    @GetMapping("/seller")
    public String showSellerInquiryList() {
        return "inquiry/seller-inquiry";
    }
}
