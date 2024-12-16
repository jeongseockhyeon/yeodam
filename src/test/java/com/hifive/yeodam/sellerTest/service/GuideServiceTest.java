package com.hifive.yeodam.sellerTest.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.GuideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GuideServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private GuideService guideService;

    private Guide guide;
    private Seller seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Auth auth = new Auth(null, "email@email.com", "password", "010-1234-1234");
        seller = new Seller(null, auth, "Company", "Owner", "Company Bio");
        guide = new Guide(null, seller, "Guide", "1997-09-01", "Male", "Bio");
    }

    @Test
    void createGuideTest() {
        // given
        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        // when
        Guide createdGuide = guideService.createGuide(guide);

        // then
        assertNotNull(createdGuide);
        assertEquals("Guide", createdGuide.getName());
        verify(guideRepository, times(1)).save(any(Guide.class));
    }

    @Test
    void updateGuideTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setBio("Updated Bio");

        when(guideRepository.findById(anyLong())).thenReturn(Optional.of(guide));
        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        // when
        Guide updatedGuide = guideService.updateGuide(1L, updateRequest);

        // then
        assertNotNull(updatedGuide);
        assertEquals("Updated Name", updatedGuide.getName());
        assertEquals("Updated Bio", updatedGuide.getBio());
        verify(guideRepository, times(1)).findById(1L);
        verify(guideRepository, times(1)).save(any(Guide.class));
    }

    @Test
    void updateGuideNotFoundTest() {
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

    @Test
    void deleteGuideTest() {
        doNothing().when(guideRepository).deleteById(anyLong());

        guideService.deleteGuide(1L);

        verify(guideRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllGuidesTest() {
        // given
        when(guideRepository.findAll()).thenReturn(List.of(guide));

        // when
        List<Guide> guides = guideService.getAllGuides();

        // then
        assertNotNull(guides);
        assertEquals(1, guides.size());
        assertEquals("Guide", guides.get(0).getName());
        verify(guideRepository, times(1)).findAll();
    }

    @Test
    void getGuideByIdTest() {
        // given
        when(guideRepository.findById(anyLong())).thenReturn(Optional.of(guide));

        // when
        Guide foundGuide = guideService.getGuideById(1L);

        // then
        assertNotNull(foundGuide);
        assertEquals("Guide", foundGuide.getName());
        verify(guideRepository, times(1)).findById(1L);
    }

    @Test
    void getGuideByIdNotFoundTest() {
        // given
        when(guideRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guideService.getGuideById(1L);
        });

        assertEquals("가이드를 찾을 수 없습니다.", exception.getMessage());
        verify(guideRepository, times(1)).findById(1L);
    }

    @Test
    void getGuidesByCompanyIdTest() {
        // given
        when(guideRepository.findBySellerCompanyId(anyLong())).thenReturn(List.of(guide));

        // when
        List<Guide> guides = guideService.getGuidesByCompanyId(1L);

        // then
        assertNotNull(guides);
        assertEquals(1, guides.size());
        assertEquals("Guide", guides.get(0).getName());
        verify(guideRepository, times(1)).findBySellerCompanyId(1L);
    }
}