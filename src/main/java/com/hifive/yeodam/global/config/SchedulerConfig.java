package com.hifive.yeodam.global.config;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final AuthRepository authRepository;
    private final SellerService sellerService;
    private final GuideService guideService;
    private final UserService userService;

    /**
     * 만료 날짜가 된 인증 정보 삭제
     * 매일 자정에 스케줄링이 동작
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteAuth() {
        // 삭제해야할 인증 정보 찾기
        log.info("탈퇴 후 30일 지난 인증 정보 삭제 스케줄러 동작");
        List<Auth> expiredAuth = authRepository.findByExpirationDate(LocalDate.now());
        log.info("만료된 인증 정보 size: {}", expiredAuth.size());

        for (Auth auth : expiredAuth) {
            if(auth.getRole().toString().equals("SELLER")) {
                guideService.deleteAllGuides(auth);
                sellerService.deleteSellerContent(auth);
            }
            else if(auth.getRole().toString().equals("USER"))
                userService.deleteUserContent(auth);

            authRepository.deleteById(auth.getId());
        }
        log.info("만료된 인증 정보 삭제 완료");
    }
}

