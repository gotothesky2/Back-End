package hackerthon.likelion13th.canfly.login.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 유효성 검사
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    // Authorization 헤더의 "Bearer " 접두어를 상수로 정의
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenUtils jwtTokenUtils;
    private final ObjectMapper objectMapper;

    // 필터링 로직
    // JWT 추출->유효성 검사
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Authorization 헤더에서 JWT 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authHeader 확인: {}", authHeader);

        // 헤더에 JWT가 포함되어 있는지 확인
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String[] parts = authHeader.split(" ");
            if (parts.length < 2) { // Bearer 접두어 뒤에 토큰이 존재하지 않을 경우
                log.warn("Authorization Header 형식이 잘못되었습니다.");
                jwtExceptionHandler(response, ErrorCode.TOKEN_INVALID);
                return;
            }
            String token = parts[1];
            log.debug("토큰 검증 시작: {}", token);

            try {
                jwtTokenUtils.parseClaims(token);
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                log.info("JWT 서명이 잘못되었습니다. {}", e.getMessage());
                jwtExceptionHandler(response, ErrorCode.TOKEN_INVALID);
                return;
            } catch (ExpiredJwtException e) {
                log.info("JWT 토큰이 만료되었습니다. {}", e.getMessage());
                jwtExceptionHandler(response, ErrorCode.TOKEN_EXPIRED);
                return;
            } catch (UnsupportedJwtException e) {
                log.info("지원되지 않는 토큰입니다. {}", e.getMessage());
                jwtExceptionHandler(response, ErrorCode.TOKEN_INVALID);
                return;
            } catch (IllegalArgumentException e) {
                log.info("잘못된 토큰입니다. {}", e.getMessage());
                jwtExceptionHandler(response, ErrorCode.TOKEN_INVALID);
                return;
            }
        }
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // JWT 검증 오류 응답 처리
    public void jwtExceptionHandler(HttpServletResponse response, ErrorCode error) {
        response.setStatus(error.getHttpStatus().value()); // HTTP 상태 코드 설정
        response.setContentType("application/json");       // 응답 타입 설정
        response.setCharacterEncoding("UTF-8");           // 응답 문자 인코딩 설정
        log.info("필터 에러 처리: {}", error.getReason().getMessage());
        try {
            // JSON 응답 생성
            objectMapper.writeValue(response.getWriter(), error.getReason());
        } catch (IOException e) {
            log.error("응답 생성 중 오류 발생: {}", e.getMessage());
        }
    }
}