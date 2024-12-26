package com.hifive.yeodam.sellerTest.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.seller.controller.GuideController;
import com.hifive.yeodam.seller.dto.GuideJoinRequest;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideControllerTest {

    @Mock
    private GuideService guideService;

    @Mock
    private SellerService sellerService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private GuideController guideController;

    private Guide guide;
    private Seller seller;
    private Auth auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auth = new Auth(1L, "email@email.com", "password", RoleType.SELLER);
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
        String viewName = guideController.createGuide(joinRequest, authentication);

        // then
        assertEquals("redirect:/guides/list", viewName);
        verify(guideService, times(1)).createGuide(joinRequest, seller);
    }

    // 가이드 등록 실패
    @Test
    void createGuideTest_fail_invalidData() {
        // given
        GuideJoinRequest joinRequest = new GuideJoinRequest(null, null, null, null, null);
        when(guideService.createGuide(joinRequest, seller)).thenThrow(new IllegalArgumentException("Invalid data"));

        // when
        assertThrows(IllegalArgumentException.class, () -> guideController.createGuide(joinRequest, authentication));

        // then
        verify(guideService, times(1)).createGuide(any(GuideJoinRequest.class), any(Seller.class));
    }

    // 가이드 정보 수정 성공
    @Test
    void updateGuideTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest("Updated Name", "01087654321", "Updated Bio");
        Guide updatedGuide = new Guide(1L, seller, "Updated Name", LocalDate.of(2002, 2, 25), "M", "01087654321", "Updated Bio");

        when(guideService.updateGuide(anyLong(), any(GuideUpdateRequest.class))).thenReturn(updatedGuide);

        // when
        var response = guideController.updateGuide(1L, updateRequest);

        // then
        assertEquals("redirect:/guides/list", response);
        verify(guideService, times(1)).updateGuide(anyLong(), any(GuideUpdateRequest.class));
    }

    // 가이드 정보 수정 실패
    @Test
    void updateGuideTest_fail_notFound() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest("Updated Name", "01012345678", "Updated Bio");

        when(guideService.updateGuide(999L, updateRequest)).thenThrow(new IllegalArgumentException("Guide not found"));

        // when
        assertThrows(IllegalArgumentException.class, () -> guideController.updateGuide(999L, updateRequest));

        // then
        verify(guideService, times(1)).updateGuide(anyLong(), any(GuideUpdateRequest.class));
    }

    // 가이드 삭제 성공
    @Test
    void deleteGuideTest() {
        // given
        doNothing().when(guideService).deleteGuide(anyLong());

        // when
        var response = guideController.deleteGuide(1L);

        // then
        assertEquals("redirect:/guides/list", response);
        verify(guideService, times(1)).deleteGuide(1L);
    }

    // 가이드 삭제 실패
    @Test
    void deleteGuideTest_fail_notFound() {
        // given
        doThrow(new IllegalArgumentException("Guide not found")).when(guideService).deleteGuide(anyLong());

        // when
        assertThrows(IllegalArgumentException.class, () -> guideController.deleteGuide(999L));

        // then
        verify(guideService, times(1)).deleteGuide(anyLong());
    }

    // 회사 아이디로 가이드 조회 성공
    @Test
    void getGuidesByCompanyIdTest() {
        // given
        List<Guide> guides = List.of(guide);
        when(guideService.getGuidesByCompanyId(seller.getCompanyId())).thenReturn(guides);

        // when
        String viewName = guideController.getGuidesByCompanyId(authentication, model);

        // then
        assertEquals("seller/guidesList", viewName);
        verify(model, times(1)).addAttribute("companyId", seller.getCompanyId());
        verify(model, times(1)).addAttribute("guides", guides);
    }

    // 가이드 등록 페이지 호출
    @Test
    void getGuideRegisterPageTest() {
        // when
        String viewName = guideController.getGuideRegisterPage();

        // then
        assertEquals("seller/guideJoin", viewName);
    }
}