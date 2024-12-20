package com.hifive.yeodam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/join")
    public String joinView() {

        return "join";
    }

    @GetMapping("/login")
    public String loginView() {

        return "login";
    }
}
