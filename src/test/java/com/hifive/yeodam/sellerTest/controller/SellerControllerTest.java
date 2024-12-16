package com.hifive.yeodam.sellerTest.controller;

import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.controller.SellerController;
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

class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController sellerController;

    private Seller seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seller = new Seller(null, "Company", "Owner", "Company Bio");
    }

    @Test
    void createSellerTest() {
        when(sellerService.createSeller(any(Seller.class))).thenReturn(seller);

        ResponseEntity<Seller> response = sellerController.createSeller(seller);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Company", response.getBody().getCompanyName());
        verify(sellerService, times(1)).createSeller(seller);
    }

    @Test
    void updateSellerTest() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest();
        updateRequest.setCompanyName("Updated Company");
        updateRequest.setOwner("Updated Owner");
        updateRequest.setBio("Updated Bio");

        Seller updatedSeller = new Seller(1L, "Updated Company", "Updated Owner", "Updated Bio");

        // when
        when(sellerService.updateSeller(anyLong(), any(SellerUpdateRequest.class))).thenReturn(updatedSeller);

        ResponseEntity<Seller> response = sellerController.updateSeller(1L, updateRequest);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Company", response.getBody().getCompanyName());
        assertEquals("Updated Owner", response.getBody().getOwner());
        assertEquals("Updated Bio", response.getBody().getBio());
        verify(sellerService, times(1)).updateSeller(anyLong(), any(SellerUpdateRequest.class));
    }

    @Test
    void deleteSellerTest() {
        doNothing().when(sellerService).deleteSeller(anyLong());

        ResponseEntity<Void> response = sellerController.deleteSeller(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(sellerService, times(1)).deleteSeller(1L);
    }
}