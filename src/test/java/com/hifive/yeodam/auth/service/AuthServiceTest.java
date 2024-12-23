package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.entity.Role;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.repository.RoleRepository;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService target;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BindingResult errorResult;

    private static final String email = "emailName@domain.com";
    private static final String password = "1234";
    private static final String name = "son";
    private static final String nickname = "sonny";
    private static final String phone = "01011112222";
    private static final LocalDate birthDate = LocalDate.of(2000, 3, 16);

    @Test
    public void 인증등록실패_이미인증정보존재() throws Exception{
        //given
        doReturn(true).when(authRepository).existsByEmail(email);

        //when
        AuthException result = assertThrows(AuthException.class,
                () -> target.addAuth(new JoinRequest(email, password, name, nickname, phone, birthDate, "M")));

        //then
        assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL_JOIN);
    }

    @Test
    public void 인증등록성공() throws Exception{
        //given
        doReturn("encryptedPwd").when(passwordEncoder).encode(password);

        doReturn(Auth.builder()
                .id(-1L)
                .email(email)
                .password("encryptedPwd")
                .build()).when(authRepository).save(any(Auth.class));

        doReturn(new Role(auth(), RoleType.USER)).when(roleRepository).save(any(Role.class));

        //when
        Auth result = target.addAuth(new JoinRequest(email, password, name, nickname, phone, birthDate, "M"));

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isNotNull();
    }

    @Test
    public void 중복이메일체크() throws Exception {
        //given
        doReturn(true).when(authRepository).existsByEmail(email);

        //when
        boolean result = target.checkEmail(email);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 이메일중복시에러값추가() throws Exception{
        //given
        JoinRequest request = JoinRequest.builder().email(email).build();

        doReturn(true).when(authRepository).existsByEmail(email);

        //when
        target.checkDuplicatedEmail(request, errorResult);

        //then

        //verify
        verify(errorResult, times(1)).addError(any(FieldError.class));
    }

    @Test
    public void 인증조회실패_인증정보존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(authRepository).findById(-1L);

        //when
        AuthException result = assertThrows(AuthException.class, () -> target.getAuth(-1L));

        //then
        assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.AUTH_NOT_FOUND);
    }

    @Test
    public void 인증조회성공() throws Exception{
        //given
        doReturn(Optional.of(Auth.builder()
                .email(email)
                .build())).when(authRepository).findById(-1L);

        //when
        Auth result = target.getAuth(-1L);

        //then
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    public void 인증수정실패_인증정보존재하지않음() throws Exception{
        //given
        UserUpdateRequest request = UserUpdateRequest.builder().build();
        doReturn(Optional.empty()).when(authRepository).findById(-1L);

        //when
        AuthException result = assertThrows(AuthException.class, () -> target.updateAuth(-1L, request));

        //then
        assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.AUTH_NOT_FOUND);
    }

    @Test
    public void 인증수정성공() throws Exception{
        //given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .password("updatedPwd")
                .build();

        doReturn(Optional.of(Auth.builder().build()))
                .when(authRepository).findById(-1L);

        doReturn("encryptedPwd").when(passwordEncoder).encode("updatedPwd");

        //when
        Auth result = target.updateAuth(-1L, request);

        //then
        assertThat(result.getPassword()).isNotNull();
    }

    private Auth auth() {
        return Auth.builder()
                .id(-1L)
                .email(email)
                .password(password)
                .build();
    }
}
