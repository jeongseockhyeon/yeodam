package com.hifive.yeodam.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inquires")
public class InquiryController {

    @GetMapping("/write")
    public String getCreateInquiryPage () {
        return "inquiry/inquiry-add";
    }


}
