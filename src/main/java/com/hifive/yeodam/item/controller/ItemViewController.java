package com.hifive.yeodam.item.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/item")
@Controller
public class ItemViewController {
    @GetMapping
    public String item(Model model) {
        return "item/items";
    }
    @GetMapping("/add")
    public String addItem(Model model) {
        return "item/itemAdd";
    }
}
