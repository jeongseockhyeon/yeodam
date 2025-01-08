package com.hifive.yeodam.user.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.review.domain.Review;
import com.hifive.yeodam.review.repository.ReviewRepository;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
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
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ReviewRepository reviewRepository;

    public UserResponse addUser(JoinRequest request, Auth auth) {

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(CustomErrorCode.DUPLICATED_NICKNAME_JOIN);
        }

        User user = User.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .phone(request.getPhone())
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

    public void checkDuplicatedNickname(UserUpdateRequest request, BindingResult result) {
        if (userRepository.existsByNickname(request.getNickname())) {
            result.addError(new FieldError("userUpdateRequest", "nickname", "이미 존재하는 닉네임입니다"));
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
        User user = optionalUser.orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long authId, UserUpdateRequest request) {

        Optional<User> optionalUser = userRepository.findByAuthId(authId);
        User user = optionalUser.orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        user.update(request.getName(), request.getNickname(), request.getPhone());

        return new UserResponse(user);
    }

    public void deleteUser(Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }

    public UserResponse getUserByAuth(Auth auth) {

        Optional<User> optionalUser = userRepository.findByAuthId(auth.getId());

        User user = optionalUser.orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return new UserResponse(user);
    }

    public void deleteUserContent(Auth auth) {

        // 삭제할 유저 찾기
        User deleteUser = userRepository.findByAuthId(auth.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 할당할 임의의 유저
        User anonymousUser = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 장바구니 삭제
        List<Cart> deleteCarts = cartRepository.findByAuthWithItemsAndImages(auth);
        cartRepository.deleteAll(deleteCarts);

        // 리뷰 삭제, 리뷰 이미지는 자동 삭제
        List<Review> deleteReviews = reviewRepository.findAllByUserId(deleteUser.getId());
        reviewRepository.deleteAll(deleteReviews);

        // 주문에 임의의 유저 할당
        List<Order> findOrders = orderRepository.findByUserId(deleteUser.getId());
        for (Order order : findOrders) {
            order.updateUser(anonymousUser);
        }

        // 유저 삭제
        userRepository.delete(deleteUser);
    }
}
