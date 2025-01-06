package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.seller.dto.GuideJoinRequest;
import com.hifive.yeodam.seller.dto.GuideResDto;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.tour.repository.TourGuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GuideService {

    private final SellerService sellerService;
    private final GuideRepository guideRepository;
    private final ReservationRepository reservationRepository;
    private final TourGuideRepository tourGuideRepository;

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
        Guide guide = guideRepository.findById(id).orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));
        Guide delete = guideRepository.findById(1L).orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));
        List<Reservation> reservationList = reservationRepository.findByGuide(guide);
        for(Reservation reservation : reservationList) {
            reservation.changeGuide(delete);
        }
        tourGuideRepository.deleteByGuideId(id);
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

    // 가이드 전체 삭제
    public void deleteAllGuides(Auth auth) {
        Seller seller = sellerService.getSellerByAuth(auth);
        Guide delete = guideRepository.findById(1L).orElseThrow(() -> new RuntimeException("가이드를 찾을 수 없습니다."));
        List<Guide> guides = guideRepository.findBySellerCompanyId(seller.getCompanyId());
        for(Guide guide : guides) {
            List<Reservation> reservationList = reservationRepository.findByGuide(guide);
            for(Reservation reservation : reservationList) {
                reservation.changeGuide(delete);
            }
            tourGuideRepository.deleteByGuideId(guide.getGuideId());
            guideRepository.delete(guide);
        }
    }

    @Transactional(readOnly = true)
    public List<GuideResDto> getGuideByCompanyFilteringExisting(Auth auth) {
        Seller seller = sellerService.getSellerByAuth(auth);
        List<Guide> guides = guideRepository.findBySellerCompanyId(seller.getCompanyId());

        // 이미 tourGuide 테이블에 등록된 가이드 아이디를 조회
        List<Long> existingGuideIds = tourGuideRepository.findGuideIdsByCompanyId(seller.getCompanyId());

        return guides.stream()
                .filter(guide -> !existingGuideIds.contains(guide.getGuideId())) // tourGuide에 등록되지 않은 가이드만 필터링
                .map(GuideResDto::new)
                .toList();
    }
}
