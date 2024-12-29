package com.hifive.yeodam.user.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> addUser(@Valid JoinRequest request, BindingResult result) {

        authService.checkDuplicatedEmail(request, result);
        userService.checkDuplicatedNickname(request, result);

        if (result.hasErrors()) {

            Map<String, String> errorMessages = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errorMessages.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorMessages);
        }

        Auth auth = authService.addAuth(request);
        UserResponse userResponse = userService.addUser(request, auth);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {

        List<UserResponse> users = userService.getUserList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {

        UserResponse userResponse = userService.getUser(id);

        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                           @RequestBody UserUpdateRequest request) {

        UserResponse userResponse = userService.updateUser(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/email-check")
    @ResponseBody
    public ResponseEntity<Boolean> emailCheck(@RequestBody String userEmail) {

        boolean isDuplicated = authService.checkEmail(userEmail);
        return ResponseEntity.ok(isDuplicated);
    }

    @PostMapping("/nickname-check")
    @ResponseBody
    public ResponseEntity<Boolean> nicknameCheck(@RequestBody String nickname) {

        boolean isDuplicated = userService.checkNickname(nickname);
        return ResponseEntity.ok(isDuplicated);
    }
}

