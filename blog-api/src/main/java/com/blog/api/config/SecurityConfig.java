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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpMethod;
import jakarta.servlet.http.HttpServletResponse;


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
                    // 읽기 전용 엔드포인트는 인증 없이 접근 허용
                    .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**").permitAll()
                    // 쓰기 관련 엔드포인트는 인증 필요
                    .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/categories").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/categories/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/categories/**").authenticated()
                    // OAuth2 로그인 관련 엔드포인트
                    .requestMatchers("/login/**", "/oauth2/**").permitAll()
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated();
            })
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(apiAuthenticationEntryPoint())
            )
            .oauth2Login(oauth2 -> {
                oauth2
                    .successHandler(oauth2SuccessHandler())
                    .failureUrl("https://nohyeongo.github.io/api/auth/login-error.html?reason=oauth_failure");
            })
            .build();
    }

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            String requestURI = request.getRequestURI();
            String acceptHeader = request.getHeader("Accept");
            
            // API 요청인지 확인
            if (requestURI.startsWith("/api/")) {
                log.warn("🚫 API 요청에 대한 인증 실패: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"인증이 필요합니다. 먼저 로그인해주세요.\", \"loginUrl\": \"/oauth2/authorization/github\"}");
            } else {
                // 웹 페이지 요청인 경우 OAuth2 로그인 페이지로 리다이렉트
                response.sendRedirect("/oauth2/authorization/github");
            }
        };
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

            // API 요청인지 확인 (Accept 헤더나 요청 경로로 판단)
            String acceptHeader = request.getHeader("Accept");
            String requestUrl = request.getRequestURL().toString();
            
            if (acceptHeader != null && acceptHeader.contains("application/json")) {
                // API 요청인 경우 - 원래 요청으로 리다이렉트하지 않고 JSON 응답
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": true, \"message\": \"로그인 성공\"}");
                return;
            }

            // 웹 페이지 요청인 경우 기존 로직
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
