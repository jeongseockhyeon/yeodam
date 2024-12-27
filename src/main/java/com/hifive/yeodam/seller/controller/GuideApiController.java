package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guides")
public class GuideApiController {

    private final GuideService guideService;

    @GetMapping("/list")
    public ResponseEntity<?> getGuideListByCompany(@AuthenticationPrincipal Auth auth){
        return ResponseEntity.ok(guideService.getGuideByCompany(auth));
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
}
