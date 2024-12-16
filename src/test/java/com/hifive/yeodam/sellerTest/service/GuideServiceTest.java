package com.hifive.yeodam.sellerTest.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private GuideService guideService;

    private Guide guide;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Seller seller = new Seller(1L, "Company", "Owner", "Bio");
        guide = new Guide(null, seller, "Guide", "1997-09-01", "Male", "Bio");
    }

    @Test
    void createGuideTest() {
        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        Guide createdGuide = guideService.createGuide(guide);

        assertNotNull(createdGuide);
        assertEquals("Guide", createdGuide.getName());
        verify(guideRepository, times(1)).save(guide);
    }

    @Test
    void updateGuideTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setBio("Updated Bio");

        // when
        when(guideRepository.findById(anyLong())).thenReturn(java.util.Optional.of(guide));
        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        Guide updatedGuide = guideService.updateGuide(1L, updateRequest);

        // then
        assertEquals("Updated Name", updatedGuide.getName());
        assertEquals("Updated Bio", updatedGuide.getBio());
        verify(guideRepository, times(1)).save(any(Guide.class));
    }

    @Test
    void deleteGuideTest() {
        doNothing().when(guideRepository).deleteById(anyLong());

        guideService.deleteGuide(1L);

        verify(guideRepository, times(1)).deleteById(1L);
    }
}