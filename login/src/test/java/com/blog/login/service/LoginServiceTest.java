package com.blog.login.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @Mock
    private OAuth2User oAuth2User;

    private LoginService loginService;

    private static final String ADMIN_GITHUB_ID = "NohYeongO";

    @BeforeEach
    void setUp() {
        loginService = new LoginService(ADMIN_GITHUB_ID);
    }

    @Test
    @DisplayName("관리자 확인 - 관리자 GitHub ID일 때 true 반환")
    void isAdmin_AdminUser_ReturnsTrue() {
        // given
        given(oAuth2User.getAttribute("login")).willReturn(ADMIN_GITHUB_ID);

        // when
        boolean result = loginService.isAdmin(oAuth2User);

        // then
        assertThat(result).isTrue();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("관리자 확인 - 일반 사용자 GitHub ID일 때 false 반환")
    void isAdmin_RegularUser_ReturnsFalse() {
        // given
        String regularUserId = "regularUser";
        given(oAuth2User.getAttribute("login")).willReturn(regularUserId);

        // when
        boolean result = loginService.isAdmin(oAuth2User);

        // then
        assertThat(result).isFalse();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("관리자 확인 - GitHub ID가 null일 때 false 반환")
    void isAdmin_NullGithubId_ReturnsFalse() {
        // given
        given(oAuth2User.getAttribute("login")).willReturn(null);

        // when
        boolean result = loginService.isAdmin(oAuth2User);

        // then
        assertThat(result).isFalse();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("관리자 확인 - 대소문자 구분하여 정확히 일치해야 함")
    void isAdmin_CaseSensitive_ReturnsFalse() {
        // given
        String lowercaseAdminId = ADMIN_GITHUB_ID.toLowerCase();
        given(oAuth2User.getAttribute("login")).willReturn(lowercaseAdminId);

        // when
        boolean result = loginService.isAdmin(oAuth2User);

        // then
        assertThat(result).isFalse();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("관리자 확인 - 빈 문자열일 때 false 반환")
    void isAdmin_EmptyString_ReturnsFalse() {
        // given
        given(oAuth2User.getAttribute("login")).willReturn("");

        // when
        boolean result = loginService.isAdmin(oAuth2User);

        // then
        assertThat(result).isFalse();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("관리자 확인 - 공백 문자열일 때 false 반환")
    void isAdmin_WhitespaceString_ReturnsFalse() {
        // given
        given(oAuth2User.getAttribute("login")).willReturn("   ");

        // when
        boolean result = loginService.isAdmin(oAuth2User);

        // then
        assertThat(result).isFalse();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("관리자 확인 - 다른 관리자 GitHub ID로 설정된 경우")
    void isAdmin_DifferentAdminId() {
        // given
        String differentAdminId = "AnotherAdmin";
        LoginService serviceWithDifferentAdmin = new LoginService(differentAdminId);
        given(oAuth2User.getAttribute("login")).willReturn(differentAdminId);

        // when
        boolean result = serviceWithDifferentAdmin.isAdmin(oAuth2User);

        // then
        assertThat(result).isTrue();
        verify(oAuth2User).getAttribute("login");
    }

    @Test
    @DisplayName("생성자 - 관리자 GitHub ID 정상 설정 확인")
    void constructor_AdminGithubIdSet() {
        // given
        String testAdminId = "TestAdmin";

        // when
        LoginService service = new LoginService(testAdminId);

        // then
        // LoginService가 private 필드를 가지므로 실제 동작으로 확인
        given(oAuth2User.getAttribute("login")).willReturn(testAdminId);
        boolean result = service.isAdmin(oAuth2User);
        assertThat(result).isTrue();
    }
}
