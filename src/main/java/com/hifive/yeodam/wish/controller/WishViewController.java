package com.hifive.yeodam.wish.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.wish.dto.WishDto;
import com.hifive.yeodam.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/wishes")
@RequiredArgsConstructor
public class WishViewController {
    private final WishService wishService;

    @GetMapping
    public String wishPage(Model model,
                           @AuthenticationPrincipal Auth auth
    ) {
        if (auth == null) {
            throw new CustomException(CustomErrorCode.AUTH_NOT_FOUND);
        }

        List<WishDto> wishes = wishService.getWish(auth.getId());
        model.addAttribute("wishes", wishes);
        return "wish/wish-list";
    }
}