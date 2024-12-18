package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    public Auth addAuth(JoinRequest request) {

        if (authRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
        }

        Auth auth = Auth.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .build();

        return authRepository.save(auth);
    }

    public Auth addAuth(SellerJoinRequest joinRequest) {
        if (authRepository.existsByEmail(joinRequest.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
        }

        Auth auth = Auth.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .phone(joinRequest.getPhone())
                .build();

        return authRepository.save(auth);
    }

    public void checkDuplicatedEmail(JoinRequest joinRequest, BindingResult result) {
        if (authRepository.existsByEmail(joinRequest.getEmail())) {
            result.addError(new FieldError("joinRequest", "email", "이미 존재하는 이메일입니다"));
        }
    }

    public boolean checkEmail(String email) {
        return authRepository.existsByEmail(email);
    }
}
