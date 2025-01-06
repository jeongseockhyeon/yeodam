package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.dto.GuideJoinRequest;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guides")
public class GuideApiController {

    private final GuideService guideService;
    private final SellerService sellerService;

    @GetMapping("/list")
    public ResponseEntity<?> getGuideListByCompany(@AuthenticationPrincipal Auth auth){
        return ResponseEntity.ok(guideService.getGuideByCompany(auth));
    }

    // 가이드 등록
    @PostMapping("/join")
    public ResponseEntity<?> createGuide(@RequestBody @Valid GuideJoinRequest joinRequest, Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        Seller seller = sellerService.getSellerByAuth(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(guideService.createGuide(joinRequest, seller));
    }

    // 가이드 정보 수정
    @PutMapping("edit/{id}")
    public ResponseEntity<?> updateGuide (@PathVariable Long id, @RequestBody @Valid GuideUpdateRequest updateRequest) {
        guideService.updateGuide(id, updateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 가이드 전체 조회 (사용 X)
//    @GetMapping
//    public ResponseEntity<List<Guide>> getAllGuides() {
//        List<Guide> guides = guideService.getAllGuides();
//        return ResponseEntity.ok(guides);
//    }

    // 가이드 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<Guide> getGuideById(@PathVariable Long id) {
        Guide guide = guideService.getGuideById(id);
        return ResponseEntity.ok(guide);
    }

    //다른 상품에 등록되어있는 가이드인지 필터링 후 조회
    @GetMapping("/list/filtering")
    public ResponseEntity<?> getGuideListByCompanyFilteringExisting(@AuthenticationPrincipal Auth auth){
        return ResponseEntity.ok(guideService.getGuideByCompanyFilteringExisting(auth));
    }
}
