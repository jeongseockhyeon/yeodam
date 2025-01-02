package com.hifive.yeodam.sellerTest.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller seller;
    private Auth auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auth = new Auth(1L, "email@email.com", "password", RoleType.SELLER, null);
        seller = new Seller(null, auth, "Company", "Owner", "Company Bio", "01012345678");
    }

    // 판매자 등록 성공
    @Test
    void createSellerTest() {
        // given
        SellerJoinRequest joinRequest = mock(SellerJoinRequest.class);
        when(joinRequest.getEmail()).thenReturn("email@email.com");
        when(joinRequest.getCompanyName()).thenReturn("Company");
        when(joinRequest.getOwner()).thenReturn("Owner");

        when(authRepository.existsByEmail(anyString())).thenReturn(true);
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        // when
        Seller createdSeller = sellerService.createSeller(joinRequest, auth);

        // then
        assertNotNull(createdSeller);
        assertEquals("Company", createdSeller.getCompanyName());
        assertEquals("Owner", createdSeller.getOwner());
        verify(authRepository, times(1)).existsByEmail(anyString());
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    // 판매자 등록 실패
    @Test
    void createSellerTest_fail_invalidData() {
        // given
        SellerJoinRequest joinRequest = mock(SellerJoinRequest.class);
        when(joinRequest.getEmail()).thenReturn("email@email.com");

        when(authRepository.existsByEmail("email@email.com")).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            sellerService.createSeller(joinRequest, auth);
        });

        assertEquals(CustomErrorCode.DUPLICATED_EMAIL_JOIN, exception.getCustomErrorCode());
        verify(authRepository, times(1)).existsByEmail(anyString());
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    // 판매자 정보 수정 성공
    @Test
    void updateSellerTest() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest("password", "Updated Company", "Updated Owner", "Updated Bio", "01087654321");

        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));
        when(sellerRepository.save(seller)).thenReturn(seller);

        // when
        Seller updatedSeller = sellerService.updateSeller(1L, updateRequest);

        // then
        assertNotNull(updatedSeller);
        assertEquals("Updated Company", updatedSeller.getCompanyName());
        assertEquals("Updated Owner", updatedSeller.getOwner());
        assertEquals("Updated Bio", updatedSeller.getBio());
        assertEquals("01087654321", updatedSeller.getPhone());
        verify(sellerRepository, times(1)).findById(1L);
    }

    // 판매자 정보 수정 실패
    @Test
    void updateSellerTest_fail_notFound() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest();
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.updateSeller(1L, updateRequest);
        });

        assertEquals("판매자를 찾을 수 없습니다.", exception.getMessage());
        verify(sellerRepository, times(1)).findById(1L);
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    // 판매자 삭제
    @Test
    void deleteSellerTest() {
        doNothing().when(sellerRepository).deleteById(anyLong());

        sellerService.deleteSeller(1L);

        verify(sellerRepository, times(1)).deleteById(1L);
    }

//    // 판매자 전제 조회 (사용 X)
//    @Test
//    void getAllSellersTest() {
//        when(sellerRepository.findAll()).thenReturn(List.of(seller));
//
//        var sellers = sellerService.getAllSellers();
//
//        assertNotNull(sellers);
//        assertEquals(1, sellers.size());
//        verify(sellerRepository, times(1)).findAll();
//    }

    // 판매자 단일 조회
    @Test
    void getSellerByIdTest() {
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));

        var foundSeller = sellerService.getSellerById(1L);

        assertNotNull(foundSeller);
        assertEquals("Company", foundSeller.getCompanyName());
        verify(sellerRepository, times(1)).findById(1L);
    }

    // 판매자 단일 조회 실패
    @Test
    void getSellerByIdTest_fail_notFound() {
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.getSellerById(1L);
        });

        assertEquals("판매자를 찾을 수 없습니다.", exception.getMessage());
        verify(sellerRepository, times(1)).findById(1L);
    }

    // Auth로 판매자 조회 성공
    @Test
    void getSellerByAuthTest() {
        when(sellerRepository.findByAuthId(1L)).thenReturn(Optional.of(seller));

        var foundSeller = sellerService.getSellerByAuth(auth);

        assertNotNull(foundSeller);
        assertEquals("Company", foundSeller.getCompanyName());
        assertEquals(auth, foundSeller.getAuth());
        verify(sellerRepository, times(1)).findByAuthId(auth.getId());
    }

    // Auth로 판매자 조회 실패
    @Test
    void getSellerByAuthTest_fail_notFound() {
        when(sellerRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sellerService.getSellerByAuth(auth);
        });

        assertEquals("해당 Auth에 연결된 Seller가 없습니다.", exception.getMessage());
        verify(sellerRepository, times(1)).findByAuthId(auth.getId());
    }
}