package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/tours")
@RestController
public class TourItemApiController {
    private final TourItemService tourItemService;

    /*여행 상품 등록*/
    @PostMapping
    public ResponseEntity<TourItemResDto> save(@Valid @ModelAttribute TourItemReqDto tourItemReqDto, @AuthenticationPrincipal Auth auth) {
        return ResponseEntity.status(CREATED).body(tourItemService.saveTourItem(tourItemReqDto,auth));
    }

    /*여행 상품 조회 + 필터링*/
    @GetMapping
    public ResponseEntity<Slice<TourItemResDto>> findAll(@RequestParam(required = false) Long cursorId,
                                                         @RequestParam int pageSize,
                                                         @ModelAttribute SearchFilterDto searchFilterDto) {
        return ResponseEntity.ok(tourItemService.getSearchFilterTour(cursorId, pageSize, searchFilterDto));
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
}
