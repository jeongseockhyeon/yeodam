package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService target;

    @Mock
    private AuthRepository authRepository;

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
        doReturn(auth()).when(authRepository).save(any(Auth.class));

        //when
        Auth result = target.addAuth(new JoinRequest(email, password, name, nickname, phone, birthDate, "M"));

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(password);
        assertThat(result.getPhone()).isEqualTo(phone);
    }

    private Auth auth() {
        return Auth.builder()
                .id(-1L)
                .email(email)
                .password(password)
                .phone(phone)
                .build();
    }
}
