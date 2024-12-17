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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    public Auth addAuth(JoinRequest request) {

        if(checkDuplicatedEmail(request.getEmail())) {
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
        checkDuplicatedEmail(joinRequest.getEmail());

        Auth auth = Auth.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .phone(joinRequest.getPhone())
                .build();

        return authRepository.save(auth);
    }

    public boolean checkDuplicatedEmail(String email) {
        return authRepository.existsByEmail(email);
    }
}
