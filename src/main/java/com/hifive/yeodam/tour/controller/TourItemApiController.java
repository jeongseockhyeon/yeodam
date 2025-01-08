package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.tour.dto.*;
import com.hifive.yeodam.tour.service.TourItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/tours")
@RestController
@Slf4j
public class TourItemApiController {
    private final TourItemService tourItemService;

    /*여행 상품 등록*/
    @PostMapping
    public ResponseEntity<?> save(@Valid @ModelAttribute TourItemReqDto tourItemReqDto,BindingResult result, @AuthenticationPrincipal Auth auth) {
        if (result.hasErrors()) {
            Map<String, String> errorMessages = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                log.error(error.getDefaultMessage());
                errorMessages.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorMessages);
        }
        return ResponseEntity.status(CREATED).body(tourItemService.saveTourItem(tourItemReqDto,auth));
    }

    /*여행 상품 조회 + 필터링*/
    @GetMapping
    public ResponseEntity<Slice<TourItemResDto>> findAll(@ModelAttribute SearchFilterDto searchFilterDto) {
        return ResponseEntity.ok(tourItemService.getSearchFilterTour(searchFilterDto));
    }

    /*업체별 여행 상품 조회*/
    @GetMapping("/seller-tour")
    public ResponseEntity<Slice<TourItemResDto>> findSellerTour(@RequestParam(required = false) Long cursorId,
                                                                @RequestParam int pageSize,
                                                                @AuthenticationPrincipal Auth auth) {
        return ResponseEntity.ok(tourItemService.findBySeller(cursorId, pageSize, auth));
    }
    /*여행 상품 단일 조회*/
    @GetMapping("/{id}")
    public ResponseEntity<TourItemResDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tourItemService.findById(id));
    }

    /*여행 상품 수정*/
    @PatchMapping("/{id}")
    public ResponseEntity<TourItemResDto> update(@PathVariable Long id, @ModelAttribute TourItemUpdateReqDto tourItemUpdateReqDto) {
        return ResponseEntity.ok(tourItemService.update(id, tourItemUpdateReqDto));
    }

    /*여행 상품 삭제*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*여행 상품 내 가이드 예약 일정 불러오기*/
    @GetMapping("/{id}/reservation")
    public ResponseEntity<List<ReservationInTourResDto>> findReservationByGuide(@PathVariable Long id) {
        return ResponseEntity.ok(tourItemService.findReservationByGuide(id));
    }

    /*여행 상품 등록 시 이미 다른 여행 상품에 등록되어 있는 가이드인지 검사*/
    @PostMapping("/{id}/guide-check")
    public ResponseEntity<Void> checkGuide(@PathVariable Long id) {
        boolean isDuplicatedGuide = tourItemService.checkDuplicateGuide(id);
        if (isDuplicatedGuide) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
