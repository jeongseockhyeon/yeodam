package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RequestMapping("/tour")
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
}
