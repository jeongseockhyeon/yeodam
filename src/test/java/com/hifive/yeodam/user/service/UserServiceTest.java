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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private AuthRepository authRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void 유저등록실패_이미인증정보존재() throws Exception{
        //given
        doReturn(true).when(authRepository).existsByEmail(email);

        //when
        AuthException result = assertThrows(AuthException.class,
                () -> target.addUser(joinRequest, auth()));

        //then
        assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_AUTH_JOIN);
    }

    @Test
    public void 유저등록성공() throws Exception{
        //given
        doReturn(false).when(authRepository).existsByEmail(email);
        doReturn(user()).when(userRepository).save(any(User.class));

        //when
        User result = target.addUser(joinRequest, auth());

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getBirthDate()).isEqualTo(birthDate);
        assertThat(result.getGender()).isEqualTo("M");

        //verify
        verify(authRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void 유저와인증의연관관계저장() throws Exception{
        //given
        doReturn(false).when(authRepository).existsByEmail(email);
        doReturn(user()).when(userRepository).save(any(User.class));

        //when
        User result = target.addUser(joinRequest, auth());

        //then
        assertThat(result.getAuth().getId()).isEqualTo(auth().getId());

        //verify
        verify(authRepository, times(1)).existsByEmail(email);
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
        List<User> result = target.getUserList();

        //then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 회원상세조회실패_회원이존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(userRepository).findById(-1L);

        //when
        UserException result = assertThrows(UserException.class, () -> target.getUser(-1L));

        //then
        assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_FOUND);
    }

    @Test
    public void 회원상세조회성공() throws Exception{
        //given
        doReturn(Optional.of(user())).when(userRepository).findById(-1L);

        //when
        User result = target.getUser(-1L);

        //then
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getBirthDate()).isEqualTo(birthDate);
    }

    @Test
    public void 회원수정실패_회원존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(userRepository).findById(-1L);

        //when
        UserException result = assertThrows(UserException.class, () -> target.updateUser(-1L, new UserUpdateRequest()));

        //then
        assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_FOUND);
    }
    
    @Test
    public void 회원수정성공() throws Exception{
        //given
        doReturn(Optional.of(user())).when(userRepository).findById(-1L);
        doReturn(User.builder().name("kim").nickname("kim12")
                .build())
                .when(userRepository).save(any(User.class));

        //when
        User result = target.updateUser(-1L, userUpdateRequest());

        //then
        assertThat(result.getName()).isEqualTo("kim");
        assertThat(result.getNickname()).isEqualTo("kim12");
    }

    @Test
    public void 회원삭제실패_회원존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(userRepository).findById(-1L);

        //when
        UserException result = assertThrows(UserException.class, () -> target.deleteUser(-1L));

        //then
        assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_FOUND);
    }

    @Test
    public void 회원삭제성공() throws Exception{
        //given
        doReturn(Optional.of(user())).when(userRepository).findById(-1L);

        //when
        target.deleteUser(-1L);

        //then
    }

    private User user(){
        return User.builder()
                .id(-1L)
                .name(name)
                .birthDate(birthDate)
                .gender("M")
                .auth(auth())
                .build();
    }

    private Auth auth(){
        return Auth.builder()
                .id(-1L)
                .email(email)
                .password(password)
                .phone(phone)
                .build();
    }

    private UserUpdateRequest userUpdateRequest(){
        return UserUpdateRequest.builder()
                .name("kim")
                .nickname("kim12")
                .build();
    }
}
