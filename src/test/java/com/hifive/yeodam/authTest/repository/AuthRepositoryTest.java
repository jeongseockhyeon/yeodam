package com.hifive.yeodam.authTest.repository;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.global.config.QueryDSLConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

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
                .build();

        //when
        Auth result = authRepository.save(auth);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(password);
    }
    
    @Test
    public void 인증이존재하는지테스트() throws Exception{
        //given
        Auth auth = Auth.builder()
                .email(email)
                .password(password)
                .build();
        
        //when
        authRepository.save(auth);
        Boolean result = authRepository.existsByEmail(email);
        
        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 만료날짜인인증잘가져오는지() throws Exception{
        //given
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        Auth auth1 = Auth.builder().expirationDate(today).build();
        Auth auth2 = Auth.builder().expirationDate(yesterday).build();
        Auth auth3 = Auth.builder().expirationDate(tomorrow).build();

        authRepository.save(auth1);
        authRepository.save(auth2);
        authRepository.save(auth3);

        //when
        List<Auth> result = authRepository.findByExpirationDate(today);

        //then
        assertThat(result.size()).isEqualTo(1);
    }

}