package com.hifive.yeodam.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailsService authService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers("/", "/login", "/join", "/logout", "/order/**", "/api/**", "tours/**", "/carts/**").permitAll()
                        .requestMatchers("/users/join", "/users/email-check", "/users/nickname-check", "/users/password-check").permitAll()
                        .requestMatchers("/sellers", "/sellers/join", "/sellers/check-email").permitAll()
                        .requestMatchers("/inquiries/**").permitAll()
                        .requestMatchers("/admins/**").hasRole("ADMIN")
                        .requestMatchers("/api/carts/sync").authenticated()
                        .requestMatchers("/api/carts/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(auth -> auth
                        .accessDeniedPage("/access-denied")
                )
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지
                        .usernameParameter("email")
                        .successHandler(loginSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(rm -> rm
                        .tokenValiditySeconds(60 * 60 * 24)
                        .key("uniqueAndSecret")
                        .rememberMeParameter("remember-me"))
                .logout(logout -> logout
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/")
                        .permitAll()
                ).csrf(csrf -> csrf.disable())
                .build();
    }

    // 로그인 시 인증 처리할 관리자 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder
                = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(authService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    //비로그인 장바구니 이용 시 인증 성공 후 장바구니로 리다이렉트
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            String redirectUrl = request.getParameter("redirectUrl");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                response.sendRedirect(redirectUrl); // 장바구니 페이지로 이동
            } else {
                response.sendRedirect("/"); // 기본 리다이렉트 URL
            }
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
