package com.hifive.yeodam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/join")
    public String joinView() {

        return "joinSelect";
    }

    @GetMapping("/login")
    public String loginView() {

        return "login";
    }

//    // 로그인 페이지 보기
//    @GetMapping("/login")
//    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
//        if (error != null) {
//            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
//        }
//        return "login";
//    }
}
