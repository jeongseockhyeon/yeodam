package com.hifive.yeodam.auth.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    private static final LocalDate deleteDate = LocalDate.now().plusDays(30);

    @PostMapping("/email-check")
    public ResponseEntity<Void> emailCheck(@RequestBody String userEmail) {

        boolean isDuplicated = authService.checkEmail(userEmail);

        if (isDuplicated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/expiration")
    public ResponseEntity<Void> addExpirationDate(@AuthenticationPrincipal Auth auth) {

        authService.updateExpiration(auth, deleteDate);
        log.info("인증 만료 날짜 설정: {}", deleteDate);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/expiration")
    public ResponseEntity<Void> removeExpirationDate(@AuthenticationPrincipal Auth auth) {

        authService.updateExpiration(auth, null);
        log.info("인증 만료 날짜 삭제");

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/get-date")
    public ResponseEntity<Map<String,Integer>> getExpirationDate() {

        Map<String, Integer> deleteDateValues = new HashMap<>();
        deleteDateValues.put("year", deleteDate.getYear());
        deleteDateValues.put("month", deleteDate.getMonthValue());
        deleteDateValues.put("day", deleteDate.getDayOfMonth());

        return ResponseEntity.ok(deleteDateValues);
    }
}
