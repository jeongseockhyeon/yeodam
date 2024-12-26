package com.hifive.yeodam.item.controller;

import com.hifive.yeodam.item.dto.ActiveUpdateDto;
import com.hifive.yeodam.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
public class ActiveUpdateApiController {
    private final ItemService itemService;

    @PatchMapping("/{id}/active")
    public ResponseEntity<String> updateActive(@PathVariable Long id, @RequestBody ActiveUpdateDto activeUpdateDto) {
        itemService.updateActive(id,activeUpdateDto);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

}
