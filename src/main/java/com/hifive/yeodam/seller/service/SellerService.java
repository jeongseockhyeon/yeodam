package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.inquiry.service.InquiryService;
import com.hifive.yeodam.item.service.ItemService;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final AuthRepository authRepository;
    private final InquiryService inquiryService;
    private final ItemService itemService;

    // 판매자 등록
    @Transactional(rollbackFor = CustomException.class)
    public Seller createSeller(SellerJoinRequest joinRequest, Auth auth) {
        if(!authRepository.existsByEmail(joinRequest.getEmail())) {
            throw new CustomException(CustomErrorCode.DUPLICATED_EMAIL_JOIN);
        }

        Seller seller = Seller.builder()
                .auth(auth)
                .companyName(joinRequest.getCompanyName())
                .owner(joinRequest.getOwner())
                .bio(joinRequest.getBio())
                .phone(joinRequest.getPhone())
                .build();

        return sellerRepository.save(seller);
    }

    // 판매자 정보 수정
    @Transactional
    public Seller updateSeller(Long id, SellerUpdateRequest updateRequest) {
        Seller existingSeller = sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));

        existingSeller.update(updateRequest.getCompanyName(), updateRequest.getOwner(), updateRequest.getBio(), updateRequest.getPhone());

        return existingSeller;
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

    // 판매자 관련 컨텐츠 분리
    public void deleteSellerContent(Auth auth) {
        Seller seller = sellerRepository.findByAuthId(auth.getId()).orElseThrow(() -> new IllegalArgumentException("해당 Auth에 연결된 Seller가 없습니다."));

        itemService.changeCompany(seller);
        inquiryService.changeAuth(auth);

        sellerRepository.delete(seller);
    }
}