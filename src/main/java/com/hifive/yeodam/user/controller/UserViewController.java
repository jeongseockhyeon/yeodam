package com.hifive.yeodam.user.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        return "user/join";
    }

    @PostMapping("/join")
    public String userJoin(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult result) {

        authService.checkDuplicatedEmail(joinRequest, result);
        userService.checkDuplicatedNickname(joinRequest, result);

        if (result.hasErrors()) {
            return "user/join";
        }

        Auth auth = authService.addAuth(joinRequest);
        userService.addUser(joinRequest, auth);

        return "redirect:/login";
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
    public String myPage(Authentication authentication, Model model) {

        Auth auth = (Auth) authentication.getPrincipal();
        User user = userService.getUserByAuth(auth);

        model.addAttribute("user", user);

        return "user/detail";
    }

    @GetMapping("/{id}")
    public String userEditView(@PathVariable Long id, Model model) {

        UserResponse userResponse = userService.getUser(id);
        Auth auth = authService.getAuth(userResponse.getAuth().getId());

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .email(auth.getEmail())
                .password(auth.getPassword())
                .name(userResponse.getName())
                .nickname(userResponse.getNickname())
                .phone(userResponse.getPhone())
                .birthDate(userResponse.getBirthDate())
                .gender(userResponse.getGender())
                .build();

        model.addAttribute("userId", id);
        model.addAttribute("user", userUpdateRequest);

        return "user/edit";
    }

    @PostMapping("/{id}")
    public String userEdit(@PathVariable Long id,
                           @Valid @ModelAttribute("user") UserUpdateRequest request, BindingResult result, Model model) {

        if(result.hasErrors()) {
            model.addAttribute("userId", id);
            return "user/edit";
        }

        UserResponse user = userService.updateUser(id, request);
        authService.updateAuth(user.getAuth().getId(), request);

        return "redirect:/users/myPage";
    }

    @PostMapping("/email-check")
    @ResponseBody
    public String emailCheck(@RequestBody String userEmail) {

        userEmail = userEmail.trim().replace("\"", "");
        if (!userEmail.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-z]+)+$")) {
            return "invalid";
        }

        if (authService.checkEmail(userEmail)) {
            return "duplicated";
        } else {
            return "ok";
        }
    }

    @PostMapping("/password-check")
    @ResponseBody
    public String passwordCheck(@RequestBody String password) {

        password = password.trim().replace("\"", "");
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$")) {
            return "invalid";
        } else {
            return "ok";
        }
    }

    @PostMapping("/nickname-check")
    @ResponseBody
    public String nicknameCheck(@RequestBody String nickname) {

        nickname = nickname.trim().replace("\"", "");
        if (userService.checkNickname(nickname)) {
            return "duplicated";
        } else {
            return "ok";
        }
    }
}
