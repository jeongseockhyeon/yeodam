package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/api/tours")
@RestController
public class TourItemAPIController {
    private final TourItemService tourItemService;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TourItemReqDto tourItemReqDto) {
        return ResponseEntity.status(CREATED).body(tourItemService.saveTourItem(tourItemReqDto));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(tourItemService.findAll());
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
