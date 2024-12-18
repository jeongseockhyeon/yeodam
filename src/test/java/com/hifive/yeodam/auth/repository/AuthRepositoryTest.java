package com.hifive.yeodam.auth.repository;

import com.hifive.yeodam.QueryDSLConfig;
import com.hifive.yeodam.auth.entity.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({QueryDSLConfig.class})
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    private static final String email = "emailName@domain.com";
    private static final String password = "Passw0rd!";
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

//    @Test
//    public void 비밀번호암호화저장() throws Exception{
//        //given
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encryptedPwd = passwordEncoder.encode(password);
//
//        Auth auth = Auth.builder()
//                .id(-1L)
//                .email(email)
//                .password(encryptedPwd)
//                .phone(phone)
//                .build();
//
//        //when
//        Auth result = authRepository.save(auth);
//
//        //then
//        assertThat(passwordEncoder.matches(password, result.getPassword())).isTrue();
//    }
}