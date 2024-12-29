package com.hifive.yeodam.authTest.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.auth.service.AuthDetailService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AuthDetailServiceTest {

    @InjectMocks
    private AuthDetailService target;

    @Mock
    private AuthRepository authRepository;

    private static final String email = "test123@gmail.com";
    private static final String password = "Passw0rd!";

    @Test
    public void 로그인실패_이메일틀림() throws Exception{
        //given
        doReturn(Optional.empty()).when(authRepository).findByEmail(email);

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.loadUserByUsername(email));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.AUTH_NOT_FOUND);
    }

    @Test
    public void 로그인성공() throws Exception{
        //given
        doReturn(Optional.of(Auth.builder().
                email(email)
                .build()))
                .when(authRepository).findByEmail(email);

        //when
        UserDetails result = target.loadUserByUsername(email);

        //then
        assertThat(result.getUsername()).isEqualTo(email);
    }

}