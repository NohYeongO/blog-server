package com.blog.api.config;

import com.blog.api.validation.AdminValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdminValidation adminValidation;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> {
                auth
                    .anyRequest().permitAll();
            })
            .oauth2Login(oauth2 -> {
                oauth2
                    .successHandler(oauth2SuccessHandler())
                    .failureUrl("https://nohyeongo.github.io/api/auth/login-error.html?reason=oauth_failure");
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

            boolean isAdmin = adminValidation.isAdminUser(user);
            log.info("🔑 관리자 권한: {}", isAdmin);

            if (!isAdmin) {
                String failUrl = UriComponentsBuilder
                        .fromUriString("https://nohyeongo.github.io/login/error.html")
                        .queryParam("reason", "not_admin")
                        .build()
                        .toUriString();
                response.sendRedirect(failUrl);
                return;
            }

            String successUrl = UriComponentsBuilder
                    .fromUriString("https://nohyeongo.github.io/login/success.html")
                    .queryParam("githubId", githubId)
                    .queryParam("name", name)
                    .queryParam("role", "ADMIN")
                    .build()
                    .toUriString();

            response.sendRedirect(successUrl);
        };
    }
}
