package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sellers")
public class SellerApiController {

    private final SellerService sellerService;
    private final AuthService authService;

    // 판매자 가입
    @PostMapping("/join")
    public ResponseEntity<?> createSeller(@RequestBody @Valid SellerJoinRequest joinRequest) {
        Auth auth = authService.addAuth(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerService.createSeller(joinRequest, auth));
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = authService.checkEmail(email);
        return ResponseEntity.ok(isDuplicate);
    }

    // 판매자 정보 수정
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateSeller(@PathVariable Long id, @RequestBody @Valid SellerUpdateRequest updateRequest) {
        Seller updatedSeller = sellerService.updateSeller(id, updateRequest);
        Auth auth = updatedSeller.getAuth();
        authService.updateAuth(auth.getId(), updateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
}
