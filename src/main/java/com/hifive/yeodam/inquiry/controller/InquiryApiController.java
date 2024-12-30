package com.hifive.yeodam.inquiry.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.inquiry.dto.AddInquiryRequest;
import com.hifive.yeodam.inquiry.service.InquiryService;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryApiController {

    private final InquiryService inquiryService;
    private final UserService userService;
    private final SellerService sellerService;

    @PostMapping("/add")
    public ResponseEntity<?> createInquiry (@RequestBody @Valid AddInquiryRequest request, Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.createInquiry(request, auth));
    }

    @GetMapping("/user/data")
    public ResponseEntity<?> getInquiryListByUser(Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        return ResponseEntity.ok(inquiryService.getInquiryByAuth(auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long id, Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        inquiryService.deleteInquiry(id, auth);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/seller")
//    public ResponseEntity<?> getInquiryListBySeller(Authentication authentication) {
//        Auth auth = (Auth) authentication.getPrincipal();
//        return ResponseEntity.ok(inquiryService.getInquiryByAuth(auth));
//    }
}