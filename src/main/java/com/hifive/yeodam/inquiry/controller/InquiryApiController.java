package com.hifive.yeodam.inquiry.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.inquiry.dto.AddInquiryRequest;
import com.hifive.yeodam.inquiry.dto.InquiryResponse;
import com.hifive.yeodam.inquiry.service.InquiryService;
import com.hifive.yeodam.item.service.ItemService;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryApiController {

    private final InquiryService inquiryService;
    private final SellerService sellerService;
    private final ItemService itemService;

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

    @GetMapping("/seller/data")
    public ResponseEntity<?> getInquiryListBySeller(Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        Seller seller = sellerService.getSellerByAuth(auth);
        List<Long> itemIds = itemService.getItemsBySeller(seller.getCompanyId());
        List<InquiryResponse> inquiries = inquiryService.getInquiriesByItemIdsExcludingSeller(itemIds, auth);
        return ResponseEntity.ok(inquiries);
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<?> answerInquiry(@PathVariable Long id, @RequestBody AddInquiryRequest request, Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        inquiryService.answerInquiry(id, request, auth);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/answer")
    public ResponseEntity<?> getAnswerInquiry(@PathVariable Long id) {
        InquiryResponse answer = new InquiryResponse(inquiryService.getAnswerInquiry(id));
        return ResponseEntity.ok(answer);
    }
}