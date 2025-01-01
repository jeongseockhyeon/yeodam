package com.hifive.yeodam.admin.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admins")
@Controller
public class AdminViewController {

    private final UserService userService;

    public AdminViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/manage")
    public String managePage(@AuthenticationPrincipal Auth auth, Model model) {
        UserResponse userResponse = userService.getUserByAuth(auth);
        model.addAttribute("admin", userResponse);
        return "admin/manage-page";
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

        return "admin/admin-edit";
    }

    @GetMapping("/category-manage")
    public String categoryManagePage(){
        return "admin/category-manage";
    }

}
