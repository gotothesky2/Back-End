package hackerthon.likelion13th.canfly.login.auth.jwt;

import hackerthon.likelion13th.canfly.login.auth.dto.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// JWT 기능 구현
@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey; // JWT 서명 키
    private final JwtParser jwtParser; // JWT 파서
    private final int accessExpirationTime; // Access Token 유효기간(초 단위)
    private final int refreshExpirationTime; // Refresh Token 유효기간(초 단위)

    // JwtTokenUtils 생성자
    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.accessExpirationTime}") int accessExpirationTime,
            @Value("${jwt.refreshExpirationTime}") int refreshExpirationTime
    ) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // 서명 키 초기화
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.signingKey).build(); // JWT` 파서 초기화
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    // 사용자 정보 기반 ->  claim의 형태로 변환 -> JWT 생성
    public JwtDto generateToken(UserDetails userDetails) {
        log.info("JWT 생성 시작: 사용자 {}", userDetails.getUsername());
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims accessTokenClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(accessExpirationTime)));
        String accessToken = Jwts.builder()
                .setClaims(accessTokenClaims)
                .claim("authorities", authorities)
                .signWith(signingKey)
                .compact();
        Claims refreshTokenClaims = Jwts.claims()
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshExpirationTime)));
        String refreshToken = Jwts.builder()
                .setClaims(refreshTokenClaims)
                .signWith(signingKey)
                .compact();

        log.info("JWT 생성 완료: AccessToken={}, RefreshToken={}", accessToken, refreshToken);

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT 유효성 검증
    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // JWT 파싱 -> Claims -> 원래 형태로
    public Claims parseClaims(String token) {
        log.info("JWT 파싱: {}", token);
        return jwtParser.parseClaimsJws(token).getBody();
    }

    // Claims -> 권한 정보 추출
    public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims) {
        String authoritiesString = claims.get("authorities", String.class);
        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}