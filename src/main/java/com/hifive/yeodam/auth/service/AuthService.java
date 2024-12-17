package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.user.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    public Auth addAuth(JoinRequest request) {

        checkDuplicatedEmail(request.getEmail());

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

    public void checkDuplicatedEmail(String email) {
        if(authRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
        }
    }
}
