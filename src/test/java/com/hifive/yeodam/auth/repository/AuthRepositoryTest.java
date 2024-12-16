package com.hifive.yeodam.auth.repository;

import com.hifive.yeodam.auth.entity.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    private static final String email = "emailName@domain.com";
    private static final String password = "1234";
    private static final String phone = "01011112222";

    @Test
    public void 인증등록() throws Exception {
        //given
        Auth auth = Auth.builder()
                .email(email)
                .password(password)
                .phone(phone)
                .build();

        //when
        Auth result = authRepository.save(auth);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(password);
        assertThat(result.getPhone()).isEqualTo(phone);
    }
    
    @Test
    public void 인증이존재하는지테스트() throws Exception{
        //given
        Auth auth = Auth.builder()
                .email(email)
                .password(password)
                .phone(phone)
                .build();
        
        //when
        authRepository.save(auth);
        Boolean result = authRepository.existsByEmail(email);
        
        //then
        assertThat(result).isTrue();
    }

}