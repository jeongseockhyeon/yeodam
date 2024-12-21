package com.hifive.yeodam.item.controller;

import com.hifive.yeodam.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class itemApiController {
    private final ItemService itemService;

    @GetMapping()
    public ResponseEntity<List<String>> getItems() {
        return ResponseEntity.ok(itemService.findAllItemType());
    }
}
