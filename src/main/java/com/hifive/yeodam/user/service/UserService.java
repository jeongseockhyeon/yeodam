package com.hifive.yeodam.user.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.user.exception.UserErrorResult;
import com.hifive.yeodam.user.exception.UserException;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public User getUser(Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        return user;
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        user.setName(request.getName());
        user.setNickname(request.getNickname());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
