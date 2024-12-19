package com.hifive.yeodam.tour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tours")
public class TourItemViewController {

    @GetMapping
    public String tourItemView() {
        return "tour/tours";
    }
    @GetMapping("/add")
    public String tourItemAdd() {
        return "tour/tourAdd";
    }
}
