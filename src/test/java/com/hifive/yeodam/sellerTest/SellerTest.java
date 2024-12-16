package com.hifive.yeodam.sellerTest;

import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SellerTest {

    @Autowired
    private SellerService sellerService;

    // 판매자 등록
    @Test
    public void createSellerTest() {
        // given
        Seller seller = new Seller(null, "create", "Ellie", "Legend seller");

        // when
        Seller createdSeller = sellerService.createSeller(seller);

        // then
        assertNotNull(createdSeller);
        assertEquals("create", createdSeller.getCompanyName());
        assertEquals("Ellie", createdSeller.getOwner());
    }

    // 판매자 단일 조회
    @Test
    public void getSellerByIdTest() {
        // given
        Seller seller = new Seller(null, "get", "Ellie", "Legend seller");
        sellerService.createSeller(seller);

        // when
        Long sellerId = seller.getCompanyId();
        Seller foundSeller = sellerService.getSellerById(sellerId);

        // then
        assertNotNull(foundSeller);
        assertEquals(sellerId, foundSeller.getCompanyId());
        assertEquals("Ellie", foundSeller.getOwner());
    }

    // 판매자 정보 수정
    @Test
    public void updateSellerTest() {
        // given
        Seller originalSeller = new Seller(null, "original", "Ellie", "Legend seller");
        sellerService.createSeller(originalSeller);

        SellerUpdateRequest updateRequest = new SellerUpdateRequest();
        updateRequest.setCompanyName("update");
        updateRequest.setOwner("Elice");
        updateRequest.setBio("The company was taken away");

        // when
        Long sellerId = originalSeller.getCompanyId();
        Seller result = sellerService.updateSeller(sellerId, updateRequest);

        // then
        assertEquals("update", result.getCompanyName());
        assertEquals("Elice", result.getOwner());
        assertEquals("The company was taken away", result.getBio());
    }

    // 판매자 삭제
    @Test
    public void deleteSellerTest() {
        // given
        Seller seller = new Seller(null, "delete", "Ellie", "Legend seller");
        sellerService.createSeller(seller);

        // when
        Long sellerId = seller.getCompanyId();
        sellerService.deleteSeller(sellerId);

        // then
        assertThrows(RuntimeException.class, () -> sellerService.getSellerById(sellerId));
    }
}