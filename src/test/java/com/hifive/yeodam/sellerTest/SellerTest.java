package com.hifive.yeodam.sellerTest;

import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SellerTest {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private AuthService authService;

    // 판매자 등록 테스트
    @Test
    public void createSellerTest() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest(
                "email1@test.com", "password123", "010-1234-5678",
                "company1", "owner1", "Bio1"
        );

        // when
        Auth auth = authService.addAuth(joinRequest); // 인증 정보 생성
        Seller savedSeller = sellerService.createSeller(joinRequest, auth);

        // then
        assertNotNull(savedSeller);
        assertEquals("company1", savedSeller.getCompanyName());
        assertEquals("owner1", savedSeller.getOwner());
        assertEquals("Bio1", savedSeller.getBio());
    }

    // 이메일 중복으로 판매자 등록 실패 테스트
    @Test
    public void createSellerWithDuplicateEmailTest() {
        // given
        SellerJoinRequest joinRequest1 = new SellerJoinRequest("email1@test.com", "password123", "010-1234-5678", "company1", "owner1", "Bio1");
        SellerJoinRequest joinRequest2 = new SellerJoinRequest("email1@test.com", "password456", "010-8765-4321", "company2", "owner2", "Bio2");
        Auth auth1 = authService.addAuth(joinRequest1);
        sellerService.createSeller(joinRequest1, auth1);

        // when & then
        assertThrows(AuthException.class, () -> {
            Auth auth2 = authService.addAuth(joinRequest2); // 중복 이메일로 Auth 생성 시 예외 발생
            sellerService.createSeller(joinRequest2, auth2);
        });
    }

    // 판매자 정보 수정 테스트
    @Test
    public void updateSellerTest() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest(
                "email1@test.com", "password123", "010-1234-5678",
                "company1", "owner1", "Bio1"
        );
        Auth auth = authService.addAuth(joinRequest);
        Seller savedSeller = sellerService.createSeller(joinRequest, auth);

        SellerUpdateRequest updateRequest = new SellerUpdateRequest("updatedCompany", "updatedOwner", "Updated Bio");

        // when
        Seller updatedSeller = sellerService.updateSeller(savedSeller.getCompanyId(), updateRequest);

        // then
        assertEquals("updatedCompany", updatedSeller.getCompanyName());
        assertEquals("updatedOwner", updatedSeller.getOwner());
        assertEquals("Updated Bio", updatedSeller.getBio());
    }

    // 존재하지 않는 판매자 수정 시 예외 처리
    @Test
    public void updateSellerWithNonExistentSellerTest() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest("company", "owner", "Bio");

        // when & then
        assertThrows(RuntimeException.class, () -> {
            sellerService.updateSeller(999L, updateRequest); // 존재하지 않는 ID로 수정 시 예외 발생
        });
    }

    // 판매자 삭제 테스트
    @Test
    public void deleteSellerTest() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest(
                "email1@test.com", "password123", "010-1234-5678",
                "company1", "owner1", "Bio1"
        );
        Auth auth = authService.addAuth(joinRequest);
        Seller savedSeller = sellerService.createSeller(joinRequest, auth);

        // when
        sellerService.deleteSeller(savedSeller.getCompanyId());

        // then
        assertThrows(RuntimeException.class, () -> sellerService.getSellerById(savedSeller.getCompanyId())); // 삭제 후 조회 시 예외 발생
    }

    // 판매자 존재하지 않음으로 삭제 시 예외 처리
    @Test
    public void deleteNonExistentSellerTest() {
        // when & then
        assertThrows(RuntimeException.class, () -> {
            sellerService.deleteSeller(999L); // 존재하지 않는 ID로 삭제 시 예외 발생
        });
    }

    // 판매자 단일 조회 테스트
    @Test
    public void getSellerByIdTest() {
        // given
        SellerJoinRequest joinRequest = new SellerJoinRequest(
                "email1@test.com", "password123", "010-1234-5678",
                "company1", "owner1", "Bio1"
        );
        Auth auth = authService.addAuth(joinRequest);
        Seller savedSeller = sellerService.createSeller(joinRequest, auth);

        // when
        Seller foundSeller = sellerService.getSellerById(savedSeller.getCompanyId());

        // then
        assertNotNull(foundSeller);
        assertEquals(savedSeller.getCompanyId(), foundSeller.getCompanyId());
        assertEquals("company1", foundSeller.getCompanyName());
    }

    // 판매자 전체 조회 테스트
    @Test
    public void getAllSellersTest() {
        // given
        SellerJoinRequest joinRequest1 = new SellerJoinRequest("email1@test.com", "password123", "010-1234-5678", "company1", "owner1", "Bio1");
        SellerJoinRequest joinRequest2 = new SellerJoinRequest("email2@test.com", "password456", "010-8765-4321", "company2", "owner2", "Bio2");
        authService.addAuth(joinRequest1);
        authService.addAuth(joinRequest2);
        sellerService.createSeller(joinRequest1, authService.addAuth(joinRequest1));
        sellerService.createSeller(joinRequest2, authService.addAuth(joinRequest2));

        // when
        List<Seller> sellers = sellerService.getAllSellers();

        // then
        assertNotNull(sellers);
        assertEquals(2, sellers.size());
    }
}