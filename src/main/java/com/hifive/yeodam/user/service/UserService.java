package com.hifive.yeodam.user.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public User addUser(JoinRequest request, Auth auth) {
        if(authRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorResult.DUPLICATED_AUTH_JOIN);
        }

        User user = User.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .auth(auth)
                .build();

        return userRepository.save(user);
    }

    public List<User> getUserList() {

        return userRepository.findAll();
    }
}
