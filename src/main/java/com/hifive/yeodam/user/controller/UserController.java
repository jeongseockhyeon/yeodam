package com.hifive.yeodam.user.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid JoinRequest request) {

        Auth auth = authService.addAuth(request);
        User user = userService.addUser(request, auth);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {

        List<User> users = userService.getUserList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {

        User user = userService.getUser(id);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody UserUpdateRequest request) {

        User user = userService.updateUser(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}

