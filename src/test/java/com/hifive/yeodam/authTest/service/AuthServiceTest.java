package com.hifive.yeodam.authTest.service;

import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
        CustomException result = assertThrows(CustomException.class,
                () -> target.addAuth(new JoinRequest(email, password, name, nickname, phone, birthDate, "M")));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.DUPLICATED_EMAIL_JOIN);
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
        CustomException result = assertThrows(CustomException.class, () -> target.getAuth(-1L));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.AUTH_NOT_FOUND);
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
        CustomException result = assertThrows(CustomException.class, () -> target.updateAuth(-1L, request));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.AUTH_NOT_FOUND);
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

    @Test
    public void 만료날짜설정실패_인증존재하지않음() throws Exception{
        //given
        LocalDate ld = LocalDate.now();
        doReturn(Optional.empty()).when(authRepository).findById(-1L);

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.updateExpiration(auth(), ld));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.AUTH_NOT_FOUND);
    }

    @Test
    public void 만료기간설정되었는지확인실패_인증존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(authRepository).findById(auth().getId());

        //when
        CustomException result = assertThrows(CustomException.class, () -> target.checkExpired(auth()));

        //then
        assertThat(result.getCustomErrorCode()).isEqualTo(CustomErrorCode.AUTH_NOT_FOUND);
    }

    @Test
    public void 만료기간설정되었는지확인성공() throws Exception{
        //given
        doReturn(Optional.of(auth())).when(authRepository).findById(auth().getId());

        //when
        boolean result = target.checkExpired(auth());

        //then
        assertThat(result).isEqualTo(auth().getExpirationDate() != null);
    }

    private Auth auth() {
        return Auth.builder()
                .id(-1L)
                .email(email)
                .password(password)
                .expirationDate(null)
                .build();
    }
}
