package com.hifive.yeodam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/sellers/join", "/sellers/login", "/sellers/check-email").permitAll()
                        .requestMatchers("/sellers/myPage").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login
                        .loginPage("/sellers/login")
                        .loginProcessingUrl("/sellers/login")
                        .defaultSuccessUrl("/sellers/myPage", true)
                        .failureUrl("/sellers/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/sellers/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
