package com.hifive.yeodam.item.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/item")
@Controller
public class ItemViewController {
    @GetMapping

    /*상품 목록 페이지*/
    public String item(Model model) {
        return "item/items";
    }

    /*상품 관리 페이지*/
    @GetMapping("/manage")
    public String manageItem(Model model) {
        return "item/itemManage";
    }
}
