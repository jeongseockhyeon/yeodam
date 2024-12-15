package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.service.GuideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guides")
public class GuideController {

    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    // 가이드 등록
    @PostMapping
    public ResponseEntity<Guide> createGuide(@RequestBody Guide guide) {
        Guide savedGuide = guideService.createGuide(guide);
        return new ResponseEntity<>(savedGuide, HttpStatus.CREATED);
    }

    // 가이드 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Guide> updateGuide(@PathVariable Long id, @RequestBody Guide guide) {
        Guide updatedGuide = guideService.updateGuide(id, guide);
        return ResponseEntity.ok(updatedGuide);
    }

    // 가이드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

    // 가이드 전체 조회
    @GetMapping
    public ResponseEntity<List<Guide>> getAllGuides() {
        List<Guide> guides = guideService.getAllGuides();
        return ResponseEntity.ok(guides);
    }

    // 가이드 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<Guide> getGuideById(@PathVariable Long id) {
        Guide guide = guideService.getGuideById(id);
        return ResponseEntity.ok(guide);
    }

    // 회사 아이디로 가이드 조회
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Guide>> getGuidesByCompanyId(@PathVariable Long companyId) {
        List<Guide> guides = guideService.getGuidesByCompanyId(companyId);
        return ResponseEntity.ok(guides);
    }
}