package com.hifive.yeodam.sellerTest;

import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GuideTest {

    @Autowired
    private GuideService guideService;

    @Autowired
    private SellerService sellerService;

    // 가이드 등록
    @Test
    public void createGuideTest() {
        // given
        Seller seller = new Seller(null, "createCompany", "Min", "Legend seller");
        sellerService.createSeller(seller);
        Guide guide = new Guide(null, seller, "Min", "1990-03-26", "Male", "I like traveling!");

        // when
        Guide createdGuide = guideService.createGuide(guide);

        // then
        assertNotNull(createdGuide);
        assertEquals("Min", createdGuide.getName());
        assertEquals("Male", createdGuide.getGender());
        assertEquals("I like traveling!", createdGuide.getBio());
    }

    // 가이드 단일 조회
    @Test
    public void getGuideByIdTest() {
        // given
        Seller seller = new Seller(null, "getCompany", "Ellie", "...");
        sellerService.createSeller(seller);
        Guide guide = new Guide(null, seller, "Ellie", "2002-02-25", "Female", "Hi!");
        guideService.createGuide(guide);

        // when
        Long guideId = guide.getGuideId();
        Guide foundGuide = guideService.getGuideById(guideId);

        // then
        assertNotNull(foundGuide);
        assertEquals(guideId, foundGuide.getGuideId());
        assertEquals("Ellie", foundGuide.getName());
    }

    // 가이드 정보 수정
    @Test
    public void updateGuideTest() {
        // given
        Seller seller = new Seller(null, "Company", "Owner", "Bio");
        sellerService.createSeller(seller);

        Guide originalGuide = new Guide(null, seller, "JK", "1997-09-01", "Male", "Bio");
        guideService.createGuide(originalGuide);

        GuideUpdateRequest updateRequest = new GuideUpdateRequest();
        updateRequest.setName("Jeon");
        updateRequest.setBio("Updated Bio");

        // when
        Long guideId = originalGuide.getGuideId();
        Guide updatedGuide = guideService.updateGuide(guideId, updateRequest);

        // then
        assertEquals("Jeon", updatedGuide.getName());
        assertEquals("Updated Bio", updatedGuide.getBio());
    }

    // 가이드 삭제
    @Test
    public void deleteGuideTest() {
        // given
        Seller seller = new Seller(null, "deleteCompany", "Ellie", "Legend seller");
        sellerService.createSeller(seller);
        Guide guide = new Guide(null, seller, "Gyo", "1982-12-14", "Male", "Thank you.");
        guideService.createGuide(guide);

        // when
        Long guideId = guide.getGuideId();
        guideService.deleteGuide(guideId);

        // then
        assertThrows(RuntimeException.class, () -> guideService.getGuideById(guideId));
    }

    // 회사 아이디로 가이드 조회
    @Test
    public void getGuidesByCompanyIdTest() {
        // given
        Seller seller = new Seller(null, "Guides", "Ellie", "Legend seller");
        sellerService.createSeller(seller);
        Guide guide1 = new Guide(null, seller, "Kim", "1997-04-06", "Male", "1");
        Guide guide2 = new Guide(null, seller, "Boo", "1998-01-16", "Male", "2");
        guideService.createGuide(guide1);
        guideService.createGuide(guide2);

        // when
        Long companyId = seller.getCompanyId();
        List<Guide> guides = guideService.getGuidesByCompanyId(companyId);

        // then
        assertNotNull(guides);
        assertEquals(2, guides.size());
    }
}
