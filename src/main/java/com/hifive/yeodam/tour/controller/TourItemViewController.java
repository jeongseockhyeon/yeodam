package com.hifive.yeodam.tour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tours")
public class TourItemViewController {

    @GetMapping
    public String tourItemList() {
        return "tour/tour-list";
    }

    @GetMapping("/add")
    public String tourItemAdd() {
        return "tour/tour-add";
    }

    @GetMapping("/{id}")
    public String tourItemDetail(@PathVariable Long id) {
        return "tour/tour-detail";
    }

    @GetMapping("/{id}/update")
    public String tourItemUpdate(@PathVariable Long id) {
        return "tour/tour-update";
    }

}
