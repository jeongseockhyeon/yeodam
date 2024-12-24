package com.hifive.yeodam;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/join")
    public String joinView() {

        return "joinSelect";
    }

    // 로그인 페이지 보기
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null && error.equals("true")) {
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 잘못되었습니다. 다시 시도하세요.");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {

        return "redirect:/";
    }
}
