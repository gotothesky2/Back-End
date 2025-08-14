package hackerthon.likelion13th.canfly.login.auth.utils;

import hackerthon.likelion13th.canfly.login.auth.dto.JwtDto;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static hackerthon.likelion13th.canfly.login.auth.jwt.FrontRedirectCaptureFilter.ATTR;
import static hackerthon.likelion13th.canfly.login.auth.jwt.FrontRedirectCaptureFilter.PARAM;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserDetailsManager userDetailsManager;
    private final UserService userService;

    @Value("${frontend.base-url}")
    private String defaultRedirect;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        /* 1. OAuth2User 추출 */
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = oAuth2User.getAttribute("provider"); // kakao
        String providerId = oAuth2User.getAttribute("id");       // 4351993247
        String nickname = oAuth2User.getAttribute("name");     // 강민준
        String email = oAuth2User.getAttribute("email");

        /* 2. username 형식: {kakao}강민준 */
        String username = String.format("{%s}%s", provider, nickname);

        /* 3. 신규 사용자 저장 (+ OAuth 정보) */
        if (!userDetailsManager.userExists(username)) {
            CustomUserDetails details = CustomUserDetails.builder()
                    .username(providerId)
                    .nickname(username)
                    .email(email)
                    .provider(provider)
                    .accessToken(oAuth2User.getAttribute("oauth2AccessToken"))
                    .expireDate(oAuth2User.getAttribute("oauth2ExpiresAt"))
                    .build();
            userDetailsManager.createUser(details);
            log.info("🆕 신규 사용자 등록: {}", username);
        } else {
            log.info("👤 기존 사용자 로그인: {}", username);
        }

        /* 4. JWT 즉시 발급 (providerId 기준) */
        JwtDto jwt = userService.jwtMakeSave(providerId);
        log.info("🔑 JWT 발급 완료 | providerId={}", providerId);
        /* 5. 프로필 미완료 여부(needsProfile) 계산 */
        boolean needsProfile = userService.needsProfile(providerId);

        HttpSession session = request.getSession(false);
        String frontRedirect = null;
        if (session != null) {
            frontRedirect = (String) session.getAttribute(ATTR);
            session.removeAttribute(ATTR); // 일회성
            log.info("[FRONT_REDIRECT] loaded from session: {}", frontRedirect);
        }

        // 혹시 세션이 비어있다면 파라미터/헤더 백업
        if (frontRedirect == null) {
            frontRedirect = request.getParameter(PARAM);
            if (frontRedirect == null) {
                frontRedirect = request.getParameter("redirect_uri");
            }
            if (frontRedirect == null) {
                frontRedirect = request.getHeader("X-Front-Redirect");
            }
        }

        // 화이트리스트(정확 매칭) — 필요 시 정규화 로직 추가
        List<String> allow = List.of(
                defaultRedirect,
                "http://localhost:3000",
                "http://localhost:3000/"
        );
        if (frontRedirect == null || !allow.contains(frontRedirect)) {
            log.warn("[FRONT_REDIRECT] not allowed or missing → fallback: {}", defaultRedirect);
            frontRedirect = defaultRedirect;
        }

        String redirectUrl = UriComponentsBuilder.fromUriString(frontRedirect)
                .queryParam("accessToken", jwt.getAccessToken())
                .queryParam("needsProfile", needsProfile)
                .build()
                .toUriString();

        log.info("🔄 Redirect → {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}