package com.hifive.yeodam;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.Role;
import com.hifive.yeodam.auth.repository.RoleRepository;
import com.hifive.yeodam.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class HomeController {

    private final AuthService authService;

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

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.getAuthorities() != null) {
            Auth auth = (Auth) authentication.getPrincipal();
            String role = authService.getRole(auth);
            if ("SELLER".equals(role)) {
                model.addAttribute("isSeller", true);
            } else if ("USER".equals(role)) {
                model.addAttribute("isUser", true);
            }
        }
        return "index";
    }
}
