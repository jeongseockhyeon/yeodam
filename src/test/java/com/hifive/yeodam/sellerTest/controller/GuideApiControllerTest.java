package com.hifive.yeodam.sellerTest.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.seller.controller.GuideApiController;
import com.hifive.yeodam.seller.dto.GuideJoinRequest;
import com.hifive.yeodam.seller.dto.GuideResDto;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideApiControllerTest {

    @Mock
    private GuideService guideService;

    @Mock
    private SellerService sellerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GuideApiController guideApiController;

    private Guide guide;
    private Seller seller;
    private Auth auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auth = new Auth(1L, "email@email.com", "password", RoleType.SELLER, null);
        seller = new Seller(1L, auth, "Company", "Owner", "Company Bio", "01012345678");
        guide = new Guide(null, seller, "Name", LocalDate.of(2002, 2, 25), "M", "01012345678", "Guide Bio");

        when(authentication.getPrincipal()).thenReturn(auth);
        when(sellerService.getSellerByAuth(auth)).thenReturn(seller);
    }

    // 가이드 등록 성공
    @Test
    void createGuideTest() {
        // given
        GuideJoinRequest joinRequest = new GuideJoinRequest("Name", LocalDate.of(2002, 2, 25), "M", "01012345678", "Guide Bio");

        when(guideService.createGuide(joinRequest, seller)).thenReturn(guide);

        // when
        ResponseEntity<?> response = guideApiController.createGuide(joinRequest, authentication);

        // then
        assertEquals(201, response.getStatusCodeValue());
        verify(guideService, times(1)).createGuide(joinRequest, seller);
    }

    // 가이드 등록 실패
    @Test
    void createGuideTest_fail_invalidData() {
        // given
        GuideJoinRequest joinRequest = new GuideJoinRequest(null, null, null, null, null);
        when(guideService.createGuide(joinRequest, seller)).thenThrow(new IllegalArgumentException("Invalid data"));

        // when
        assertThrows(IllegalArgumentException.class, () -> guideApiController.createGuide(joinRequest, authentication));

        // then
        verify(guideService, times(1)).createGuide(any(GuideJoinRequest.class), any(Seller.class));
    }

    // 가이드 정보 수정 성공
    @Test
    void updateGuideTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest("Updated Name", "01087654321", "Updated Bio");

        doNothing().when(guideService).updateGuide(anyLong(), any(GuideUpdateRequest.class));

        // when
        ResponseEntity<?> response = guideApiController.updateGuide(1L, updateRequest);

        // then
        assertEquals(204, response.getStatusCodeValue());
        verify(guideService, times(1)).updateGuide(1L, updateRequest);
    }

    // 가이드 단일 조회 성공
    @Test
    void getGuideByIdTest() {
        // given
        when(guideService.getGuideById(1L)).thenReturn(guide);

        // when
        ResponseEntity<Guide> response = guideApiController.getGuideById(1L);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(guide, response.getBody());
        verify(guideService, times(1)).getGuideById(1L);
    }

    // 회사 아이디로 가이드 목록 조회 성공
    @Test
    void getGuideListByCompanyTest() {
        // given
        List<GuideResDto> guides = List.of(new GuideResDto(guide));
        when(guideService.getGuideByCompany(auth)).thenReturn(guides);

        // when
        ResponseEntity<?> response = guideApiController.getGuideListByCompany(auth);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(guides, response.getBody());
        verify(guideService, times(1)).getGuideByCompany(auth);
    }
}