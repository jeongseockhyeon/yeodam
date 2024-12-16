package com.hifive.yeodam.sellerTest.controller;

import com.hifive.yeodam.seller.controller.GuideController;
import com.hifive.yeodam.seller.dto.GuideUpdateRequest;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.service.GuideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideControllerTest {

    @Mock
    private GuideService guideService;

    @InjectMocks
    private GuideController guideController;

    private Guide guide;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        guide = new Guide(null, null, "Name", "Bio", "Male", "Guide Bio");
    }

    @Test
    void createGuideTest() {
        when(guideService.createGuide(any(Guide.class))).thenReturn(guide);

        ResponseEntity<Guide> response = guideController.createGuide(guide);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Name", response.getBody().getName());
        verify(guideService, times(1)).createGuide(guide);
    }

    @Test
    void updateGuideTest() {
        // given
        GuideUpdateRequest updateRequest = new GuideUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setBio("Updated Bio");

        Guide updatedGuide = new Guide(1L, null, "Updated Name", "1997-09-01", "Male", "Updated Bio");

        // when
        when(guideService.updateGuide(anyLong(), any(GuideUpdateRequest.class))).thenReturn(updatedGuide);

        ResponseEntity<Guide> response = guideController.updateGuide(1L, updateRequest);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("Updated Bio", response.getBody().getBio());
        verify(guideService, times(1)).updateGuide(anyLong(), any(GuideUpdateRequest.class));
    }

    @Test
    void deleteGuideTest() {
        doNothing().when(guideService).deleteGuide(anyLong());

        ResponseEntity<Void> response = guideController.deleteGuide(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(guideService, times(1)).deleteGuide(1L);
    }
}