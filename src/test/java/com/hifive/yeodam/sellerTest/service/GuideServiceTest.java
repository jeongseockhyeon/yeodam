package com.hifive.yeodam.sellerTest.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.seller.dto.GuideJoinRequest;
import com.hifive.yeodam.seller.dto.GuideResDto;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GuideServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private GuideService guideService;

    private Guide guide;
    private Seller seller;
    private Auth auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auth = new Auth(1L, "email@email.com", "password", RoleType.SELLER, null);
        seller = new Seller(1L, auth, "Company", "Owner", "Company Bio", "01012345678");
        guide = new Guide(null, seller, "Name", LocalDate.of(2002, 2, 25), "M", "01012345678", "Guide Bio");
    }

    // 가이드 등록 성공
    @Test
    void createGuideTest() {
        // given
        GuideJoinRequest joinRequest = mock(GuideJoinRequest.class);
        when(joinRequest.getName()).thenReturn("Name");

        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        // when
        Guide createdGuide = guideService.createGuide(joinRequest, seller);

        // then
        assertNotNull(createdGuide);
        assertEquals("Name", createdGuide.getName());
        verify(guideRepository, times(1)).save(any(Guide.class));
    }

    // 가이드 정보 수정 성공
    @Test
    void updateGuideTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest("Updated Name", "01087654321", "Updated Bio");

        when(guideRepository.findById(anyLong())).thenReturn(Optional.of(guide));
        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        // when
        guideService.updateGuide(1L, updateRequest);

        // then
        verify(guideRepository, times(1)).findById(1L);
    }

    // 가이드 정보 수정 실패
    @Test
    void updateGuide_fail_notFound() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest();
        when(guideRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guideService.updateGuide(1L, updateRequest);
        });

        assertEquals("가이드를 찾을 수 없습니다.", exception.getMessage());
        verify(guideRepository, times(1)).findById(1L);
        verify(guideRepository, never()).save(any(Guide.class));
    }

    // 가이드 삭제 성공
    @Test
    void deleteGuideTest() {
        doNothing().when(guideRepository).deleteById(anyLong());

        guideService.deleteGuide(1L);

        verify(guideRepository, times(1)).deleteById(1L);
    }

    // 가이드 단일 조회 성공
    @Test
    void getGuideByIdTest() {
        // given
        when(guideRepository.findById(anyLong())).thenReturn(Optional.of(guide));

        // when
        Guide foundGuide = guideService.getGuideById(1L);

        // then
        assertNotNull(foundGuide);
        assertEquals("Name", foundGuide.getName());
        verify(guideRepository, times(1)).findById(1L);
    }

    // 가이드 단일 조회 실패
    @Test
    void getGuideById_fail_notFound() {
        // given
        when(guideRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guideService.getGuideById(1L);
        });

        assertEquals("가이드를 찾을 수 없습니다.", exception.getMessage());
        verify(guideRepository, times(1)).findById(1L);
    }

    // 회사 아이디로 가이드 조회 성공
    @Test
    void getGuideByCompanyTest() {
        // given
        when(sellerService.getSellerByAuth(auth)).thenReturn(seller);
        when(guideRepository.findBySellerCompanyId(seller.getCompanyId())).thenReturn(List.of(guide));

        // when
        List<GuideResDto> guides = guideService.getGuideByCompany(auth);

        // then
        assertNotNull(guides);
        assertEquals(1, guides.size());
        GuideResDto guideResDto = guides.get(0);
        assertEquals(guide.getName(), guideResDto.getName());
        assertEquals(guide.getPhone(), guideResDto.getPhone());
        assertEquals(guide.getBio(), guideResDto.getBio());
        verify(sellerService, times(1)).getSellerByAuth(auth);
        verify(guideRepository, times(1)).findBySellerCompanyId(seller.getCompanyId());
    }
}