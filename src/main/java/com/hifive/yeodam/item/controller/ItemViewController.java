package com.hifive.yeodam.item.controller;


import com.hifive.yeodam.tour.service.TourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/item")
@RequiredArgsConstructor
@Controller
public class ItemViewController {
    private final TourItemService tourItemService;

    @GetMapping
    /*모든 종류의 상품 목록 페이지*/
    public String item() {
        return "/item/item-list";
    }

    /*업체의 상품 관리 페이지*/
/*    @GetMapping("/manage")
    public String manageItem(Model model, @AuthenticationPrincipal Auth auth) {
        model.addAttribute("tourList", tourItemService.findBySeller(auth) );
        return "item/item-manage";
    }*/
}
