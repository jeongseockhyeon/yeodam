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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> addUser(@RequestBody @Valid JoinRequest request, BindingResult result) {

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

    @PutMapping
    public ResponseEntity<?> editUser(@AuthenticationPrincipal Auth auth,
                                      @Valid @RequestBody UserUpdateRequest request, BindingResult result) {

        userService.checkDuplicatedNickname(request, result);

        if (result.hasErrors()) {
            Map<String, String> errorMessages = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errorMessages.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorMessages);
        }

        authService.updateAuth(auth.getId(), request);
        UserResponse userResponse = userService.updateUser(auth.getId(), request);

        return ResponseEntity.ok(userResponse);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/nickname-check")
    public ResponseEntity<Void> nicknameCheck(@RequestBody String nickname) {

        boolean isDuplicated = userService.checkNickname(nickname);

        if (isDuplicated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

