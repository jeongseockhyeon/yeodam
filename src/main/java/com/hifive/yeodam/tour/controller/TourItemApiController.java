package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/tours")
@RestController
public class TourItemApiController {
    private final TourItemService tourItemService;

    @PostMapping
    public ResponseEntity<?> save(@Valid @ModelAttribute TourItemReqDto tourItemReqDto, @AuthenticationPrincipal Auth auth) {
        return ResponseEntity.status(CREATED).body(tourItemService.saveTourItem(tourItemReqDto,auth));
    }

    @GetMapping
    public ResponseEntity<?> findAll(@ModelAttribute SearchFilterDto searchFilterDto) {
        return ResponseEntity.ok(tourItemService.getSearchFilterTour(searchFilterDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tourItemService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @ModelAttribute TourItemUpdateReqDto tourItemUpdateReqDto) {
        return ResponseEntity.ok(tourItemService.update(id, tourItemUpdateReqDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        tourItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
