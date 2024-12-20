package com.hifive.yeodam.auth.service;

import com.hifive.yeodam.auth.entity.Auth;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthDetailsService implements UserDetailsService {

    private final AuthService authService;

    public AuthDetailsService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Auth auth = authService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                auth.getEmail(),
                auth.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
        );
    }
}