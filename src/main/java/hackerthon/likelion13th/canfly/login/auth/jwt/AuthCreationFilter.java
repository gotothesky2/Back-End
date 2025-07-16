package hackerthon.likelion13th.canfly.login.auth.jwt;

import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 기반 인증 필터
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCreationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().startsWith("/ws-chat")) {
            filterChain.doFilter(request, response);          // ← 그냥 패스
            return;
        }
        log.info("Request URL: {}", request.getServletPath());

        // 1. Authorization 헤더에서 JWT 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Authorization Header: {}", authHeader);

        // 2. JWT가 유효한 형식인지 확인
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = extractToken(authHeader); // "Bearer " 이후의 JWT 추출
            log.debug("Extracted Token: {}", token);

            try {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication authentication;

                // 3. 특정 요청(/users/reissue)에 대해 익명 인증 처리
                if (isReissueRequest(request)) {
                    authentication = createAnonymousAuthentication();
                    log.debug("토큰 재발행을 위해 익명으로 인증 생성");
                } else {
                    // 4. JWT를 검증하고 사용자 인증 객체 생성
                    authentication = createAuthentication(token);
                    log.debug("사용자 인증 객체 생성: {}", authentication.getName());
                }

                // 5. SecurityContext에 인증 객체 저장
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("사용자 인증 객체 set 저장: {}", authentication.getName());

            } catch (Exception e) {
                log.error("인증 처리 실패.: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext(); // 실패 시 SecurityContext 초기화
            }
        }

        // 6. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더-> JWT 추출
    private String extractToken(String authHeader) {
        return authHeader.substring(7); // "Bearer " 이후의 값
    }

    // 재발급 요청 확인
    private boolean isReissueRequest(HttpServletRequest request) {
        return "/users/reissue".equals(request.getServletPath());
    }

    //익명 인증 객체 생성
    private Authentication createAnonymousAuthentication() {
        return new AnonymousAuthenticationToken(
                "key",
                "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );
    }

    // JWT 검증-> 인증 객체 생성
    private Authentication createAuthentication(String token) {
        Claims claims = jwtTokenUtils.parseClaims(token);
        return new UsernamePasswordAuthenticationToken(
                CustomUserDetails.builder()
                        .username(claims.getSubject())
                        .build(),
                token,
                jwtTokenUtils.getAuthFromClaims(claims)
        );
    }
}