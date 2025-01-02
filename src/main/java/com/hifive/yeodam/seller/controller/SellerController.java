package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.dto.GuideResDto;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;
    private final GuideService guideService;

    // 가입 페이지 보기
    @GetMapping("/join")
    public String showSellerJoinPage() {
        return "seller/seller-join";
    }

    // 수정 페이지 보기
    @GetMapping("/edit/{id}")
    public String getSellerUpdatePage (@PathVariable Long id, Model model) {
        Seller seller = sellerService.getSellerById(id);
        model.addAttribute("seller", seller);
        return "seller/seller-edit";
    }

    // 마이페이지 보기
    @GetMapping("/myPage")
    public String showMyPage(Authentication authentication, Model model) {
        Auth auth = (Auth) authentication.getPrincipal();
        Seller seller = sellerService.getSellerByAuth(auth);
        model.addAttribute("seller", seller);
        return "seller/seller-myPage";
    }

    @GetMapping("/guide-list")
    public String getGuidesByCompanyId(Authentication authentication, Model model) {
        Auth auth = (Auth) authentication.getPrincipal();
        List<GuideResDto> guides = guideService.getGuideByCompany(auth);
        model.addAttribute("guides", guides);
        return "seller/guide-list";
    }

}