package com.blog.api.config;

import com.blog.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> {
                auth
                    .anyRequest().permitAll();
            })
            .oauth2Login(oauth2 -> {
                oauth2
                    .successHandler(oauth2SuccessHandler())
                    .failureUrl("/api/auth/login-error?reason=oauth_failure");
            })
            .logout(logout -> {
                logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessUrl("/api/auth/logout-success")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID");
            })
            .build();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            String githubId = user.getAttribute("login");
            String name = user.getAttribute("name");

            log.info("🎉 OAuth2 로그인 성공:");
            log.info("👤 GitHub ID: {}", githubId);
            log.info("📝 이름: {}", name);

            boolean isAdmin = loginService.isAdmin(user);
            log.info("🔑 관리자 권한: {}", isAdmin);

            if (!isAdmin) {
                log.warn("❌ 관리자 권한 없음 - 접근 거부");
                response.sendRedirect("/api/auth/login-error?reason=not_admin");
                return;
            }
            
            response.sendRedirect("/api/auth/user");
        };
    }
}
