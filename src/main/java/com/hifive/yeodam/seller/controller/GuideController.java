package com.hifive.yeodam.seller.controller;

import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/guides")
public class GuideController {

    private final GuideService guideService;

    // 가이드 정보 수정
    @GetMapping("/edit/{id}")
    public String getGuideUpdatePage(@PathVariable Long id, Model model) {
        Guide guide = guideService.getGuideById(id);
        model.addAttribute("guide", guide);
        return "seller/guide-edit";
    }

    // 가이드 삭제
    @DeleteMapping("delete/{id}")
    public String deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return "redirect:/sellers/guide-list";
    }

    // 가이드 등록 페이지로 이동
    @GetMapping("/join")
    public String getGuideRegisterPage() {
        return "seller/guide-join";
    }
}