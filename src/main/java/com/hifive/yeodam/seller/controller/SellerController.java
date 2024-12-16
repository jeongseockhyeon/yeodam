package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthException;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;
    private final AuthService authService;

    // 판매자 등록
    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody @Valid SellerJoinRequest joinRequest) {
        Auth auth = authService.addAuth(joinRequest);
        Seller savedSeller = sellerService.createSeller(joinRequest, auth);
        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    // 판매자 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody @Valid SellerUpdateRequest updateRequest) {
        Seller updatedSeller = sellerService.updateSeller(id, updateRequest);
        return ResponseEntity.ok(updatedSeller);
    }

    // 판매자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

    // 판매자 전체 조회
    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers() {
        List<Seller> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    // 판매자 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        Seller seller = sellerService.getSellerById(id);
        return ResponseEntity.ok(seller);
    }
}