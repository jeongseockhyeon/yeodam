package com.hifive.yeodam.user.repository;

import com.hifive.yeodam.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

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

    @Test
    public void 회원조회_사이즈가0() throws Exception{
        //given

        //when
        List<User> result = userRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 회원조회_사이즈가2() throws Exception{
        //given
        User user1 = User.builder()
                .name(username)
                .birthDate(birthDate)
                .build();

        User user2 = User.builder()
                .name("kim")
                .birthDate(birthDate)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        //when
        List<User> result = userRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
    }
}
