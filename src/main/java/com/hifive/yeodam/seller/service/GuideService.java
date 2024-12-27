package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.dto.GuideJoinRequest;
import com.hifive.yeodam.seller.dto.GuideResDto;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GuideService {

    private final SellerService sellerService;
    private final GuideRepository guideRepository;

    // 가이드 등록
    @Transactional
    public Guide createGuide(GuideJoinRequest joinRequest, Seller seller) {
        Guide guide = Guide.builder()
                .seller(seller)
                .name(joinRequest.getName())
                .birth(joinRequest.getBirth())
                .gender(joinRequest.getGender())
                .phone(joinRequest.getPhone())
                .bio(joinRequest.getBio())
                .build();
        return guideRepository.save(guide);
    }

    // 가이드 정보 수정
    @Transactional
    public void updateGuide(Long id, GuideUpdateRequest updateRequest) {
        Guide existingGuide = guideRepository.findById(id).orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));
        existingGuide.update(updateRequest.getName(), updateRequest.getPhone(), updateRequest.getBio());
    }

    // 가이드 삭제
    @Transactional
    public void deleteGuide(Long id) {
        guideRepository.deleteById(id);
    }

//    // 가이드 전체 조회 (사용 X)
//    public List<Guide> getAllGuides() {
//        return guideRepository.findAll();
//    }

    // 가이드 단일 조회
    public Guide getGuideById(Long id) {
        return guideRepository.findById(id).orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));
    }

    // 회사 아이디로 가이드 조회
    @Transactional(readOnly = true)
    public List<GuideResDto> getGuideByCompany(Auth auth) {
        Seller seller = sellerService.getSellerByAuth(auth);
        List<Guide> guides =  guideRepository.findBySellerCompanyId(seller.getCompanyId());
        return guides.stream()
                .map(GuideResDto::new)
                .toList();
    }
}
