package com.hifive.yeodam;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.RoleType;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.user.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitData {

    private final InitAuth initAuth;

    @PostConstruct
    public void init() {
        initAuth.initDb();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitAuth {

        private final EntityManager em;

        public void initDb() {

            Auth findAuth = em.find(Auth.class, 1L);
            if (findAuth != null) {
                return;
            }

            Auth auth = Auth.builder()
                    .role(RoleType.NONE)
                    .build();

            em.persist(auth);

            User user = User.builder()
                    .name("알수없음")
                    .auth(auth)
                    .build();

            em.persist(user);

            Seller seller = Seller.builder()
                    .companyName("알수없음")
                    .auth(auth)
                    .build();

            em.persist(seller);

            Guide guide = Guide.builder()
                    .name("알수없음")
                    .seller(seller)
                    .build();

            em.persist(guide);
        }
    }

}
