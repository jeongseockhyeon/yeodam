package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final AuthRepository authRepository;

    // 판매자 등록
    @Transactional(rollbackFor = AuthException.class)
    public Seller createSeller(SellerJoinRequest joinRequest, Auth auth) {
        if(authRepository.existsByEmail(joinRequest.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_AUTH_JOIN);
        }

        Seller seller = new Seller();
        seller.setAuth(auth);
        seller.setCompanyName(joinRequest.getCompanyName());
        seller.setOwner(joinRequest.getOwner());
        seller.setBio(joinRequest.getBio());

        return sellerRepository.save(seller);
    }

    // 판매자 정보 수정
    @Transactional
    public Seller updateSeller(Long id, SellerUpdateRequest updateRequest) {
        Seller existingSeller = sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));

        existingSeller.setCompanyName(updateRequest.getCompanyName());
        existingSeller.setOwner(updateRequest.getOwner());
        existingSeller.setBio(updateRequest.getBio());

        return sellerRepository.save(existingSeller);
    }

    // 판매자 삭제
    @Transactional
    public void deleteSeller(Long id) {
        sellerRepository.deleteById(id);
    }

    // 판매자 전체 조회
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    // 판매자 단일 조회
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
    }
}