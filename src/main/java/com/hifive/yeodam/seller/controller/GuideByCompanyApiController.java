package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.service.GuideByCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class GuideByCompanyApiController {
    private final GuideByCompanyService guideByCompanyService;

    @GetMapping("/guide-list")
    public ResponseEntity<?> getGuideListByCompany(@AuthenticationPrincipal Auth auth){
        return ResponseEntity.ok(guideByCompanyService.getGuideByCompany(auth));
    }
}
