package com.hifive.yeodam;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/* 테스트용 더미 데이터 추후 삭제 예정*/
@Component
@RequiredArgsConstructor
public class InitData {

    private final InitUser initUser;
    private final InitUser.InitItem initItem;
    private final InitUser.InitGuide guide;

    @PostConstruct
    public void init() {
        initUser.initDb();
        initItem.initDb();
        guide.initDb();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitUser {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        public void initDb() {
            Auth auth = Auth.builder()
                    .email("123@a.com")
                    .password(passwordEncoder.encode("1234"))
                    .build();

            em.persist(auth);

            User user = User.builder()
                    .name("홍길동")
                    .nickname("길동이")
                    .phone("1234-1234")
                    .auth(auth)
                    .build();

            em.persist(user);
        }

        @Component
        @Transactional
        @RequiredArgsConstructor
        static class InitItem {

            private final EntityManager em;

            public void initDb() {
                Tour tour = Tour.builder()
                        .itemName("제주도 푸른밤")
                        .price(100)
                        .stock(1)
                        .build();

                em.persist(tour);
            }
        }

        @Component
        @Transactional
        @RequiredArgsConstructor
        static class InitGuide {

            private final EntityManager em;

            public void initDb() {
                Guide guideA = Guide.builder()
                        .bio("ㅎㅇ")
                        .birth(LocalDate.now())
                        .name("가이드1")
                        .build();

                Guide guideB = Guide.builder()
                        .bio("ㅎㅇ")
                        .birth(LocalDate.now())
                        .name("가이드2")
                        .build();

                em.persist(guideA);
                em.persist(guideB);
            }
        }
    }
}
