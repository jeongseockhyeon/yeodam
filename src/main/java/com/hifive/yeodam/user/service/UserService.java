package com.hifive.yeodam.user.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.exception.UserErrorResult;
import com.hifive.yeodam.user.exception.UserException;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse addUser(JoinRequest request, Auth auth) {

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserException(UserErrorResult.DUPLICATED_NICKNAME_JOIN);
        }

        User user = User.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .auth(auth)
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser);
    }

    public void checkDuplicatedNickname(JoinRequest joinRequest, BindingResult result) {
        if (userRepository.existsByNickname(joinRequest.getNickname())) {
            result.addError(new FieldError("joinRequest", "nickname", "이미 존재하는 닉네임입니다"));
        }
    }

    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public List<UserResponse> getUserList() {

        List<User> userList = userRepository.findAll();

        return userList.stream()
                .map(v -> new UserResponse(v))
                .collect(Collectors.toList());
    }

    public UserResponse getUser(Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        user.setName(request.getName());
        user.setNickname(request.getNickname());

        return new UserResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
