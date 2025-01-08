package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.seller.dto.SellerUpdateRequest;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Auth addAuth(JoinRequest request) {

        if (authRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(CustomErrorCode.DUPLICATED_EMAIL_JOIN);
        }

        Auth auth = Auth.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.USER)
                .build();

        return authRepository.save(auth);
    }

    @Transactional
    public Auth addAuth(SellerJoinRequest joinRequest) {
        if (authRepository.existsByEmail(joinRequest.getEmail())) {
            throw new CustomException(CustomErrorCode.DUPLICATED_EMAIL_JOIN);
        }

        Auth auth = Auth.builder()
                .email(joinRequest.getEmail())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .role(RoleType.SELLER)
                .build();

        return authRepository.save(auth);
    }

    @Transactional
    public Auth updateAuth(Long id, UserUpdateRequest request) {

        Optional<Auth> optionalAuth = authRepository.findById(id);
        Auth auth = optionalAuth.orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));

        auth.update(passwordEncoder.encode(request.getPassword()));

        return auth;
    }

    @Transactional
    public void updateAuth(Long id, SellerUpdateRequest updateRequest) {

        Optional<Auth> optionalAuth = authRepository.findById(id);
        Auth auth = optionalAuth.orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));

        auth.update(passwordEncoder.encode(updateRequest.getPassword()));
    }

    public void checkDuplicatedEmail(JoinRequest joinRequest, BindingResult result) {
        if (authRepository.existsByEmail(joinRequest.getEmail())) {
            result.addError(new FieldError("joinRequest", "email", "이미 존재하는 이메일입니다"));
        }
    }

    public boolean checkEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    public Optional<Auth> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    public Auth getAuth(Long id) {

        Optional<Auth> optionalAuth = authRepository.findById(id);

        return optionalAuth.orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));
    }

    @Transactional
    public void updateExpiration(Auth auth, LocalDate expirationDate) {

        Optional<Auth> optionalAuth = authRepository.findById(auth.getId());

        Auth findAuth = optionalAuth.orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));

        findAuth.update(expirationDate);
    }

    public boolean checkExpired(Auth auth) {

        Optional<Auth> optionalAuth = authRepository.findById(auth.getId());

        Auth findAuth = optionalAuth.orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));

        return findAuth.getExpirationDate() != null;
    }
}
