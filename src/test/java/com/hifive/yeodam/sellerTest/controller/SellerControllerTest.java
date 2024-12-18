package com.hifive.yeodam.sellerTest.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.seller.controller.SellerController;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private SellerController sellerController;

    private Seller seller;
    private Auth auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auth = new Auth(null, "email@email.com", "password", "01012341234");
        seller = new Seller(null, auth, "Company", "Owner", "Company Bio");
    }

    // 판매자 등록 성공
    @Test
    void createSellerTest() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest();
        joinRequest.setEmail("email@email.com");
        joinRequest.setPassword("password");
        joinRequest.setPhone("01012341234");
        joinRequest.setCompanyName("Company");
        joinRequest.setOwner("Owner");
        joinRequest.setBio("Company Bio");

        when(authService.addAuth(any(SellerJoinRequest.class))).thenReturn(auth);
        when(sellerService.createSeller(any(SellerJoinRequest.class), any(Auth.class))).thenReturn(seller);

        // when
        String viewName = sellerController.createSeller(joinRequest);

        // then
        assertEquals("redirect:/sellers/login", viewName);
        verify(authService, times(1)).addAuth(any(SellerJoinRequest.class));
        verify(sellerService, times(1)).createSeller(any(SellerJoinRequest.class), any(Auth.class));
    }

    // 판매자 등록 실패
    @Test
    void createSellerTest_fail_invalidData() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest();
        joinRequest.setEmail("");
        joinRequest.setPassword("password");
        joinRequest.setPhone("01012341234");
        joinRequest.setCompanyName("Company");
        joinRequest.setOwner("Owner");
        joinRequest.setBio("Company Bio");

        when(authService.addAuth(any(SellerJoinRequest.class))).thenThrow(new IllegalArgumentException("Invalid email"));

        // when
        assertThrows(IllegalArgumentException.class, () -> sellerController.createSeller(joinRequest));

        // then
        verify(authService, times(1)).addAuth(any(SellerJoinRequest.class));
        verify(sellerService, never()).createSeller(any(SellerJoinRequest.class), any(Auth.class));
    }

    // 판매자 정보 수정 성공
    @Test
    void updateSellerTest() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest();
        updateRequest.setCompanyName("Updated Company");
        updateRequest.setOwner("Updated Owner");
        updateRequest.setBio("Updated Bio");

        Seller updatedSeller = new Seller(1L, auth, "Updated Company", "Updated Owner", "Updated Bio");

        when(sellerService.updateSeller(anyLong(), any(SellerUpdateRequest.class))).thenReturn(updatedSeller);

        // when
        ResponseEntity<Seller> response = sellerController.updateSeller(1L, updateRequest);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Company", response.getBody().getCompanyName());
        assertEquals("Updated Owner", response.getBody().getOwner());
        assertEquals("Updated Bio", response.getBody().getBio());
        verify(sellerService, times(1)).updateSeller(anyLong(), any(SellerUpdateRequest.class));
    }

    // 판매자 정보 수정 실패(해당 판매자 X)
    @Test
    void updateSellerTest_fail_notFound() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest();
        updateRequest.setCompanyName("Updated Company");
        updateRequest.setOwner("Updated Owner");
        updateRequest.setBio("Updated Bio");

        when(sellerService.updateSeller(anyLong(), any(SellerUpdateRequest.class)))
                .thenThrow(new IllegalArgumentException("Seller not found"));

        // when
        assertThrows(IllegalArgumentException.class, () -> sellerController.updateSeller(1L, updateRequest));

        // then
        verify(sellerService, times(1)).updateSeller(anyLong(), any(SellerUpdateRequest.class));
    }

    // 판매자 삭제 성공
    @Test
    void deleteSellerTest() {
        // given
        doNothing().when(sellerService).deleteSeller(anyLong());

        // when
        ResponseEntity<Void> response = sellerController.deleteSeller(1L);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(sellerService, times(1)).deleteSeller(anyLong());
    }

    // 판매자 삭제 실패
    @Test
    void deleteSellerTest_fail_notFound() {
        // given
        doThrow(new IllegalArgumentException("Seller not found")).when(sellerService).deleteSeller(anyLong());

        // when
        assertThrows(IllegalArgumentException.class, () -> sellerController.deleteSeller(1L));

        // then
        verify(sellerService, times(1)).deleteSeller(anyLong());
    }

    // 판매자 전체 조회
    @Test
    void getAllSellersTest() {
        // given
        List<Seller> sellers = Collections.singletonList(seller);
        when(sellerService.getAllSellers()).thenReturn(sellers);

        // when
        ResponseEntity<List<Seller>> response = sellerController.getAllSellers();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(sellerService, times(1)).getAllSellers();
    }

    // 아이디로 판매자 조회
    @Test
    void getSellerByIdTest() {
        // given
        when(sellerService.getSellerById(anyLong())).thenReturn(seller);

        // when
        ResponseEntity<Seller> response = sellerController.getSellerById(1L);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Company", response.getBody().getCompanyName());
        verify(sellerService, times(1)).getSellerById(anyLong());
    }

    // 아이디로 판매자 조회 실패
    @Test
    void getSellerByIdTest_fail_notFound() {
        // given
        when(sellerService.getSellerById(anyLong())).thenThrow(new IllegalArgumentException("Seller not found"));

        // when
        assertThrows(IllegalArgumentException.class, () -> sellerController.getSellerById(1L));

        // then
        verify(sellerService, times(1)).getSellerById(anyLong());
    }

    // 이메일 중복 체크
    @Test
    void checkEmailDuplicateTest() {
        // given
        String email = "email@email.com";
        when(authService.checkEmail(anyString())).thenReturn(true);

        // when
        ResponseEntity<Boolean> response = sellerController.checkEmailDuplicate(email);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(authService, times(1)).checkEmail(anyString());
    }
}