package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
