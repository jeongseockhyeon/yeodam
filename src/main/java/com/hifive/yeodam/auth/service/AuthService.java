package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.Role;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.auth.repository.RoleRepository;
import com.hifive.yeodam.seller.dto.SellerJoinRequest;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public Auth addAuth(JoinRequest request) {

        if (authRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
        }

        Auth auth = Auth.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Role role = new Role(auth, RoleType.USER);
        roleRepository.save(role);

        return authRepository.save(auth);
    }

    @Transactional
    public Auth addAuth(SellerJoinRequest joinRequest) {
        if (authRepository.existsByEmail(joinRequest.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
        }

        Auth auth = Auth.builder()
                .email(joinRequest.getEmail())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .build();

        return authRepository.save(auth);
    }

    // 로그인
    public boolean authenticate(String email, String password) {
        Auth auth = authRepository.findByEmail(email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(password, auth.getPassword())) {
            return true;
        }
        return false;
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
