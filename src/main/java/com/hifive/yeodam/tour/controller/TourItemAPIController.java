package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/tours")
@Slf4j
@RestController
public class TourItemAPIController {
    private final TourItemService tourItemService;

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody TourItemReqDto tourItemReqDto) {
        return ResponseEntity.status(CREATED).body(tourItemService.saveTourItem(tourItemReqDto));
    }

    @GetMapping
    public ResponseEntity<?> findAll(@ModelAttribute SearchFilterDto searchFilterDto) {
/*        if(searchFilterDto != null){
            return ResponseEntity.ok(tourItemService.getSearchFilterTour(searchFilterDto));
        }*/
        log.info("searchFilterDto: {}", searchFilterDto.getCategory());
        return ResponseEntity.ok(tourItemService.getSearchFilterTour(searchFilterDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tourItemService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TourItemUpdateReqDto tourItemUpdateReqDto) {
        return ResponseEntity.ok(tourItemService.update(id, tourItemUpdateReqDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        tourItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
