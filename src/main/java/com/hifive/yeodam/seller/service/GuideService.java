package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.repository.GuideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GuideService {

    private final GuideRepository guideRepository;

    public GuideService(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    // 가이드 등록
    @Transactional
    public Guide createGuide(Guide guide) {
        return guideRepository.save(guide);
    }

    // 가이드 정보 수정
    @Transactional
    public Guide updateGuide(Long id, GuideUpdateRequest updateRequest) {
        Guide existingGuide = guideRepository.findById(id).orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));

        existingGuide.setName(updateRequest.getName());
        existingGuide.setBio(updateRequest.getBio());

        return guideRepository.save(existingGuide);
    }

    // 가이드 삭제
    @Transactional
    public void deleteGuide(Long id) {
        guideRepository.deleteById(id);
    }

    // 가이드 전체 조회
    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }

    // 가이드 단일 조회
    public Guide getGuideById(Long id) {
        return guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));
    }

    // 회사 아이디로 가이드 조회
    public List<Guide> getGuidesByCompanyId(Long companyId) {
        return guideRepository.findBySellerCompanyId(companyId);
    }
}
