package com.hifive.yeodam.sellerTest.service;

import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import com.hifive.yeodam.seller.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seller = new Seller(null, "Company", "Owner", "Company Bio");
    }

    @Test
    void createSellerTest() {
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller createdSeller = sellerService.createSeller(seller);

        assertNotNull(createdSeller);
        assertEquals("Company", createdSeller.getCompanyName());
        verify(sellerRepository, times(1)).save(seller);
    }

    @Test
    void updateSellerTest() {
        // given
        SellerUpdateRequest updateRequest = new SellerUpdateRequest();
        updateRequest.setCompanyName("Updated Company");
        updateRequest.setOwner("Updated Owner");
        updateRequest.setBio("Updated Bio");

        // when
        when(sellerRepository.findById(anyLong())).thenReturn(java.util.Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller updatedSeller = sellerService.updateSeller(1L, updateRequest);

        // then
        assertEquals("Updated Company", updatedSeller.getCompanyName());
        assertEquals("Updated Owner", updatedSeller.getOwner());
        assertEquals("Updated Bio", updatedSeller.getBio());
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    void deleteSellerTest() {
        doNothing().when(sellerRepository).deleteById(anyLong());

        sellerService.deleteSeller(1L);

        verify(sellerRepository, times(1)).deleteById(1L);
    }
}