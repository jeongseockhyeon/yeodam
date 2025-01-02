package com.hifive.yeodam.sellerTest.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.seller.controller.SellerApiController;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SellerApiControllerTest {

    @Mock
    private SellerService sellerService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private SellerApiController sellerApiController;

    private Seller seller;
    private Auth auth;
    private Long sellerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auth = new Auth(1L, "email@email.com", "password", RoleType.SELLER, null);
        seller = new Seller(null, auth, "Company", "Owner", "Company Bio", "01012345678");
        sellerId = 1L;
    }

    // 판매자 등록 성공
    @Test
    void createSellerTest() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest("email@email.com", "password", "01012341234", "Company", "Owner", "Company Bio");

        when(authService.addAuth(joinRequest)).thenReturn(auth);
        when(sellerService.createSeller(joinRequest, auth)).thenReturn(seller);

        // when
        ResponseEntity<?> response = sellerApiController.createSeller(joinRequest);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService, times(1)).addAuth(any(SellerJoinRequest.class));
        verify(sellerService, times(1)).createSeller(any(SellerJoinRequest.class), any(Auth.class));
    }

    // 판매자 등록 실패
    @Test
    void createSellerTest_fail_invalidData() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest("", "password", "01012341234", "Company", "Owner", "Company Bio");

        when(authService.addAuth(joinRequest)).thenThrow(new IllegalArgumentException("Invalid email"));

        // when
        assertThrows(IllegalArgumentException.class, () -> sellerApiController.createSeller(joinRequest));

        // then
        verify(authService, times(1)).addAuth(any(SellerJoinRequest.class));
        verify(sellerService, never()).createSeller(any(SellerJoinRequest.class), any(Auth.class));
    }

    // 판매자 정보 수정 성공
    @Test
    void updateSellerTest() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest("password", "Updated Company", "Updated Owner", "Updated Bio", "01087654321");

        Seller updatedSeller = new Seller(sellerId, auth, "Updated Company", "Updated Owner", "Updated Bio", "01087654321");

        when(sellerService.updateSeller(sellerId, updateRequest)).thenReturn(updatedSeller);

        // when
        ResponseEntity<?> response = sellerApiController.updateSeller(sellerId, updateRequest);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(sellerService, times(1)).updateSeller(anyLong(), any(SellerUpdateRequest.class));
        verify(authService, times(1)).updateAuth(anyLong(), any(SellerUpdateRequest.class));
    }

    // 판매자 정보 수정 실패(해당 판매자 X)
    @Test
    void updateSellerTest_fail_notFound() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest("password", "Updated Company", "Updated Owner", "Updated Bio", "01087654321");

        when(sellerService.updateSeller(999L, updateRequest))
                .thenThrow(new IllegalArgumentException("Seller not found"));

        // when
        assertThrows(IllegalArgumentException.class, () -> sellerApiController.updateSeller(999L, updateRequest));

        // then
        verify(sellerService, times(1)).updateSeller(anyLong(), any(SellerUpdateRequest.class));
    }

    // 판매자 삭제 성공
    @Test
    void deleteSellerTest() {
        // given
        doNothing().when(sellerService).deleteSeller(anyLong());

        // when
        ResponseEntity<Void> response = sellerApiController.deleteSeller(sellerId);

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
        assertThrows(IllegalArgumentException.class, () -> sellerApiController.deleteSeller(999L));

        // then
        verify(sellerService, times(1)).deleteSeller(anyLong());
    }

//    // 판매자 전체 조회 (사용 X)
//    @Test
//    void getAllSellersTest() {
//        // given
//        List<Seller> sellers = Collections.singletonList(seller);
//        when(sellerService.getAllSellers()).thenReturn(sellers);
//
//        // when
//        ResponseEntity<List<Seller>> response = sellerController.getAllSellers();
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        verify(sellerService, times(1)).getAllSellers();
//    }

    // 아이디로 판매자 조회
    @Test
    void getSellerByIdTest() {
        // given
        when(sellerService.getSellerById(sellerId)).thenReturn(seller);

        // when
        ResponseEntity<Seller> response = sellerApiController.getSellerById(sellerId);

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
        assertThrows(IllegalArgumentException.class, () -> sellerApiController.getSellerById(1L));

        // then
        verify(sellerService, times(1)).getSellerById(anyLong());
    }

    // 이메일 중복 체크
    @Test
    void checkEmailDuplicateTest() {
        // given
        String email = "email@email.com";
        when(authService.checkEmail(email)).thenReturn(true);

        // when
        ResponseEntity<Boolean> response = sellerApiController.checkEmailDuplicate(email);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(authService, times(1)).checkEmail(anyString());
    }
}