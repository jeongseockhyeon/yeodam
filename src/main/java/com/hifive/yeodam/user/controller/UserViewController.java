package com.hifive.yeodam.user.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/join")
    public String userJoinView(Model model) {

        model.addAttribute("joinRequest", new JoinRequest());

        return "user/user-join";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        logoutHandler.logout(
                request,
                response,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        return "redirect:/";
    }

    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal Auth auth, Model model) {

        UserResponse userResponse = userService.getUserByAuth(auth);

        model.addAttribute("user", userResponse);

        return "user/user-detail";
    }

    @GetMapping("/edit")
    public String userEditView(@AuthenticationPrincipal Auth auth, Model model) {

        UserResponse userResponse = userService.getUserByAuth(auth);

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .email(auth.getEmail())
                .password(auth.getPassword())
                .name(userResponse.getName())
                .nickname(userResponse.getNickname())
                .phone(userResponse.getPhone())
                .birthDate(userResponse.getBirthDate())
                .gender(userResponse.getGender())
                .build();

        model.addAttribute("userUpdateRequest", userUpdateRequest);

        return "user/user-edit";
    }
}
