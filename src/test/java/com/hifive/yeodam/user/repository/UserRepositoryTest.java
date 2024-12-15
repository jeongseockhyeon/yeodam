package com.hifive.yeodam.user.repository;

import com.hifive.yeodam.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static final String username = "son";
    private static final LocalDate birthDate = LocalDate.of(2000, 3, 16);

    @Test
    public void 회원등록() throws Exception{
        //given
        User user = User.builder()
                .name(username)
                .birthDate(birthDate)
                .gender("M")
                .build();

        //when
        User result = userRepository.save(user);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(username);
        assertThat(result.getBirthDate()).isEqualTo(birthDate);
        assertThat(result.getGender()).isEqualTo("M");
    }
}
