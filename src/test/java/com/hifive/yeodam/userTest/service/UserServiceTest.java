package com.hifive.yeodam.userTest.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import com.hifive.yeodam.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String email = "emailName@domain.com";
    private static final String password = "1234";
    private static final String name = "son";
    private static final String nickname = "sonny";
    private static final String phone = "01011112222";
    private static final LocalDate birthDate = LocalDate.of(2000, 3, 16);
    private static final JoinRequest joinRequest
            = new JoinRequest(email, password, name, nickname, phone, birthDate, "M");

    @InjectMocks
    private UserService target;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BindingResult errorResult;

    @Test
    public void 유저등록실패_닉네임존재() throws Exception{
        //given
        doReturn(true).when(userRepository).existsByNickname(nickname);

        //when
        CustomException result = assertThrows(CustomException.class,
                () -> target.addUser(joinRequest, auth()));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.DUPLICATED_NICKNAME_JOIN);
    }

    @Test
    public void 유저등록성공() throws Exception{
        //given
        doReturn(false).when(userRepository).existsByNickname(nickname);
        doReturn(user()).when(userRepository).save(any(User.class));

        //when
        UserResponse result = target.addUser(joinRequest, auth());

        //then
        assertThat(result.getId()).isEqualTo(user().getId());
        assertThat(result.getAuth().getId()).isEqualTo(auth().getId());

        //verify
        verify(userRepository, times(1)).existsByNickname(nickname);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void 유저와인증의연관관계저장() throws Exception{
        //given
        doReturn(user()).when(userRepository).save(any(User.class));

        //when
        UserResponse result = target.addUser(joinRequest, auth());

        //then
        assertThat(result.getAuth().getId()).isEqualTo(auth().getId());

        //verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void 회원목록조회() throws Exception{
        //given
        doReturn(Arrays.asList(
                User.builder().build(),
                User.builder().build(),
                User.builder().build()
        )).when(userRepository).findAll();

        //when
        List<UserResponse> result = target.getUserList();

        //then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 회원상세조회실패_회원이존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(userRepository).findById(-1L);

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.getUser(-1L));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void 회원상세조회성공() throws Exception{
        //given
        doReturn(Optional.of(user())).when(userRepository).findById(-1L);

        //when
        UserResponse result = target.getUser(-1L);

        //then
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getBirthDate()).isEqualTo(birthDate);
    }

    @Test
    public void 회원수정실패_회원존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(userRepository).findByAuthId(-1L);

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.updateUser(-1L, new UserUpdateRequest()));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.USER_NOT_FOUND);
    }
    
    @Test
    public void 회원수정성공() throws Exception{
        //given
        doReturn(Optional.of(user())).when(userRepository).findByAuthId(-1L);

        //when
        UserResponse result = target.updateUser(-1L, userUpdateRequest());

        //then
        assertThat(result.getName()).isEqualTo(userUpdateRequest().getName());
        assertThat(result.getNickname()).isEqualTo(userUpdateRequest().getNickname());
        assertThat(result.getPhone()).isEqualTo(userUpdateRequest().getPhone());
    }

    @Test
    public void 회원삭제실패_회원존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(userRepository).findById(-1L);

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.deleteUser(-1L));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void 회원삭제성공() throws Exception{
        //given
        doReturn(Optional.of(user())).when(userRepository).findById(-1L);

        //when
        target.deleteUser(-1L);

        //then
    }

    @Test
    public void 닉네임중복여부() throws Exception{
        //given
        doReturn(true).when(userRepository).existsByNickname(nickname);

        //when
        boolean result = target.checkNickname(nickname);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 닉네임중복시에러추가() throws Exception{
        //given
        JoinRequest request = JoinRequest.builder().nickname(nickname).build();

        doReturn(true).when(userRepository).existsByNickname(nickname);

        //when
        target.checkDuplicatedNickname(request, errorResult);

        //then

        //verify
        verify(errorResult, times(1)).addError(any(FieldError.class));
    }

    @Test
    public void 인증정보로회원찾기실패_회원존재하지않음() throws Exception{
        //given
        Auth auth = Auth.builder().id(-1L).build();
        doReturn(Optional.empty()).when(userRepository).findByAuthId(auth.getId());

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.getUserByAuth(auth));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void 인증정보로회원찾기() throws Exception{
        //given
        Auth auth = Auth.builder().id(-1L).build();
        User user = User.builder().auth(auth).build();

        doReturn(Optional.of(user)).when(userRepository).findByAuthId(auth.getId());

        //when
        UserResponse result = target.getUserByAuth(auth);

        //then
        assertThat(result.getAuth().getId()).isEqualTo(auth.getId());
    }

    private User user(){
        return User.builder()
                .id(-1L)
                .name(name)
                .birthDate(birthDate)
                .gender("M")
                .phone(phone)
                .auth(auth())
                .build();
    }

    private Auth auth(){
        return Auth.builder()
                .id(-1L)
                .email(email)
                .password(password)
                .build();
    }

    private UserUpdateRequest userUpdateRequest(){
        return UserUpdateRequest.builder()
                .name("kim")
                .nickname("kim12")
                .phone("01012345678")
                .build();
    }
}
