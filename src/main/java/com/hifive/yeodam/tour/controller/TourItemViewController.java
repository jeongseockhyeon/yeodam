package com.hifive.yeodam.tour.controller;

import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.service.TourItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tours")
public class TourItemViewController {

    private final TourItemService tourItemService;

    public TourItemViewController(TourItemService tourItemService) {
        this.tourItemService = tourItemService;
    }

    @GetMapping
    public String tourItemList() {
        return "tour/tours";
    }
    @GetMapping("/add")
    public String tourItemAdd() {
        return "tour/tourAdd";
    }
    @GetMapping("/{id}")
    public String tourItemDetail(@PathVariable Long id, Model model) {
        TourItemResDto tourItemResDto = tourItemService.findById(id);
        model.addAttribute("tourItemResDto", tourItemResDto);
        return "tour/tourDetailSample";
    }
}
