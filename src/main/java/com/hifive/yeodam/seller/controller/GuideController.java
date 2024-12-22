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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/guides")
public class GuideController {

    private final GuideService guideService;
    private final SellerService sellerService;

    // 가이드 등록
    @PostMapping
    public String createGuide(@ModelAttribute @Valid GuideJoinRequest joinRequest, Authentication authentication) {
        Auth auth = (Auth) authentication.getPrincipal();
        Seller seller = sellerService.getSellerByAuth(auth);
        Guide savedGuide = guideService.createGuide(joinRequest, seller);
        return "redirect:/guides/list";
    }

    // 가이드 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Guide> updateGuide(@PathVariable Long id, @ModelAttribute @Valid GuideUpdateRequest updateRequest) {
        Guide updatedGuide = guideService.updateGuide(id, updateRequest);
        return ResponseEntity.ok(updatedGuide);
    }

    // 가이드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

//    // 가이드 전체 조회 (사용 X)
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

    // 회사 아이디로 가이드 조회
    @GetMapping("/list")
    public String getGuidesByCompanyId(Authentication authentication, Model model) {
        Auth auth = (Auth) authentication.getPrincipal();
        Seller seller = sellerService.getSellerByAuth(auth);
        List<Guide> guides = guideService.getGuidesByCompanyId(seller.getCompanyId());
        model.addAttribute("companyId", seller.getCompanyId());
        model.addAttribute("guides", guides);
        return "seller/guidesList";
    }

    // 가이드 등록 페이지로 이동
    @GetMapping("/join")
    public String getGuideRegisterPage() {
        return "seller/guideJoin";
    }
}