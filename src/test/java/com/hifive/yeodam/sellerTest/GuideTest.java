package com.hifive.yeodam.sellerTest;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    private SellerJoinRequest joinRequest;

    private Auth auth;

    private Seller seller;

    @BeforeEach
    public void setUp() {
        joinRequest = new SellerJoinRequest("email@email.com", "password", "010-1234-1234", "company", "Min", "Legend seller");
        seller = sellerService.createSeller(joinRequest, new Auth());
    }

    // 가이드 등록
    @Test
    public void createGuideTest() {
        // given
        Guide guide = new Guide(null, seller, "Min", "1990-03-26", "Male", "I like traveling!");

        // when
        Guide createdGuide = guideService.createGuide(guide);

        // then
        assertNotNull(createdGuide);
        assertEquals("Min", createdGuide.getName());
        assertEquals("Male", createdGuide.getGender());
        assertEquals("I like traveling!", createdGuide.getBio());
        assertNotNull(createdGuide.getGuideId()); // Ensure guide has been assigned an ID
    }

    // 가이드 단일 조회
    @Test
    public void getGuideByIdTest() {
        // given
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
        assertNotEquals(originalGuide.getName(), updatedGuide.getName()); // Ensure the name is updated
    }

    // 가이드 삭제
    @Test
    public void deleteGuideTest() {
        // given
        Guide guide = new Guide(null, seller, "Gyo", "1982-12-14", "Male", "Thank you.");
        guideService.createGuide(guide);

        // when
        Long guideId = guide.getGuideId();
        guideService.deleteGuide(guideId);

        // then
        assertThrows(RuntimeException.class, () -> guideService.getGuideById(guideId)); // Ensure the guide is deleted
    }

    // 회사 아이디로 가이드 조회
    @Test
    public void getGuidesByCompanyIdTest() {
        // given
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
        assertTrue(guides.stream().anyMatch(guide -> guide.getName().equals("Kim")));
        assertTrue(guides.stream().anyMatch(guide -> guide.getName().equals("Boo")));
    }

    // 가이드 조회시 회사에 가이드가 없는 경우
    @Test
    public void getGuidesByCompanyIdNoGuidesTest() {
        // given
        SellerJoinRequest newRequest = new SellerJoinRequest("email2@email.com", "password", "010-1234-1234", "NoGuides", "Lee", "No guides here!");
        Seller newSeller = sellerService.createSeller(newRequest, new Auth());

        // when
        Long companyId = newSeller.getCompanyId();
        List<Guide> guides = guideService.getGuidesByCompanyId(companyId);

        // then
        assertNotNull(guides);
        assertTrue(guides.isEmpty(), "Expected no guides for this company");
    }

    // 존재하지 않는 ID로 수정 요청하면 예외 처리
    @Test
    public void updateGuideWithNonExistentIdTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest();
        updateRequest.setName("NonExistent");
        updateRequest.setBio("Non-existent guide");

        // when & then
        assertThrows(RuntimeException.class, () -> guideService.updateGuide(999L, updateRequest));
    }

    // 존재하지 않는 ID로 삭제 요청하면 예외 처리
    @Test
    public void deleteGuideWithNonExistentIdTest() {
        // when & then
        assertThrows(RuntimeException.class, () -> guideService.deleteGuide(999L));
    }

    /*
    @AfterEach
    public void tearDown() {
        guideService.deleteAll();
        sellerService.deleteAll();
    }

     */

}