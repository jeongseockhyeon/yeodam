package com.hifive.yeodam.globalTest.config;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.global.config.SchedulerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {

    @InjectMocks
    private SchedulerConfig target;

    @Mock
    private AuthRepository authRepository;

    @Test
    public void 스케줄링작업_오늘이만료날짜인경우() throws Exception{
        //given
        LocalDate today = LocalDate.now();

        doReturn(List.of(
                Auth.builder().id(-1L).expirationDate(today).build(),
                Auth.builder().id(-2L).expirationDate(today).build(),
                Auth.builder().id(-3L).expirationDate(today).build()
        )).when(authRepository).findByExpirationDate(today);

        //when
        target.deleteAuth();

        //then

        //verify
        verify(authRepository, times(1)).findByExpirationDate(any(LocalDate.class));
        verify(authRepository, times(3)).deleteById(any(Long.class));
    }
}