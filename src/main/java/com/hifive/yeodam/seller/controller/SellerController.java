package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final AuthService authService;

    // 가입 페이지 보기
    @GetMapping("/join")
    public String showSellerJoinPage() {
        return "seller/sellerJoin";
    }

    // 판매자 가입
    @PostMapping
    public String createSeller(@ModelAttribute @Valid SellerJoinRequest joinRequest) {
        Auth auth = authService.addAuth(joinRequest);
        Seller savedSeller = sellerService.createSeller(joinRequest, auth);

        return "redirect:/login";
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = authService.checkEmail(email);
        return ResponseEntity.ok(isDuplicate);
    }

    // 판매자 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @ModelAttribute @Valid SellerUpdateRequest updateRequest) {
        Seller updatedSeller = sellerService.updateSeller(id, updateRequest);
        return ResponseEntity.ok(updatedSeller);
    }

    // 판매자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

//    // 판매자 전체 조회 (사용 X)
//    @GetMapping
//    public ResponseEntity<List<Seller>> getAllSellers() {
//        List<Seller> sellers = sellerService.getAllSellers();
//        return ResponseEntity.ok(sellers);
//    }

    // 판매자 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        Seller seller = sellerService.getSellerById(id);
        return ResponseEntity.ok(seller);
    }

    // 판매자 마이페이지
    @GetMapping("/myPage")
    public String showMyPage(Authentication authentication, Model model) {
        Auth auth = (Auth) authentication.getPrincipal();
        Seller seller = sellerService.getSellerByAuth(auth);
        model.addAttribute("seller", seller);
        return "seller/myPage";
    }
}