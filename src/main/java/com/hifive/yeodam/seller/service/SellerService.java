package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.Role;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.auth.repository.RoleRepository;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;

    // 판매자 등록
    @Transactional(rollbackFor = AuthException.class)
    public Seller createSeller(SellerJoinRequest joinRequest, Auth auth) {
        if(!authRepository.existsByEmail(joinRequest.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
        }

        Seller seller = new Seller();
        seller.setAuth(auth);
        seller.setCompanyName(joinRequest.getCompanyName());
        seller.setOwner(joinRequest.getOwner());
        seller.setBio(joinRequest.getBio());
        seller.setPhone(joinRequest.getPhone());

        Role role = new Role(auth, RoleType.SELLER);
        roleRepository.save(role);

        return sellerRepository.save(seller);
    }

    // 판매자 정보 수정
    @Transactional
    public Seller updateSeller(Long id, SellerUpdateRequest updateRequest) {
        Seller existingSeller = sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));

        existingSeller.setCompanyName(updateRequest.getCompanyName());
        existingSeller.setOwner(updateRequest.getOwner());
        existingSeller.setBio(updateRequest.getBio());
        existingSeller.setPhone(updateRequest.getPhone());

        return sellerRepository.save(existingSeller);
    }

    // 판매자 삭제
    @Transactional
    public void deleteSeller(Long id) {
        sellerRepository.deleteById(id);
    }

//    // 판매자 전체 조회 (사용 X)
//    public List<Seller> getAllSellers() {
//        return sellerRepository.findAll();
//    }

    // 판매자 단일 조회
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
    }

    // Auth로 판매자 조회
    public Seller getSellerByAuth(Auth auth) {
        return sellerRepository.findByAuthId(auth.getId()).orElseThrow(() -> new IllegalArgumentException("해당 Auth에 연결된 Seller가 없습니다."));
    }
}