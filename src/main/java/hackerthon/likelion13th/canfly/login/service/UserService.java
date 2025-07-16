package hackerthon.likelion13th.canfly.login.service;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.global.utils.Redis.RedisUtil;
import hackerthon.likelion13th.canfly.login.auth.dto.JwtDto;
import hackerthon.likelion13th.canfly.login.auth.jwt.JwtTokenUtils;
import hackerthon.likelion13th.canfly.login.auth.service.JpaUserDetailsManager;
import hackerthon.likelion13th.canfly.login.converter.UserConverter;
import hackerthon.likelion13th.canfly.login.dto.UserRequestDto;
import hackerthon.likelion13th.canfly.login.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JpaUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtil redisUtil;
    // private final AmazonS3Manager amazonS3Manager;

    // 로그인

    // username으로 User찾기
    public User findUserByUserName(String userName) {
        return userRepository.findByName(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNAME));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
    }

    public Boolean checkMemberByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(UserRequestDto userReqDto) {
        // 새로운 사용자 생성
        User newUser = userRepository.save(UserConverter.saveUser(userReqDto));

        // 새로운 사용자 정보를 반환하기 전에 저장된 UserDetails를 다시 로드하여 동기화 시도
        manager.loadUserByUsername(userReqDto.getUsername());

        return newUser;
    }

    public JwtDto jwtMakeSave(String username) {
        // JWT 생성 - access & refresh
        UserDetails details
                = manager.loadUserByUsername(username);

        JwtDto jwt = jwtTokenUtils.generateToken(details); //2. access, refresh token 생성 및 발급
        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 유효기간 초단위 설정 후 db에 refresh token save
        Claims refreshTokenClaims = jwtTokenUtils.parseClaims(jwt.getRefreshToken());
        Long validPeriod
                = refreshTokenClaims.getExpiration().toInstant().getEpochSecond()
                - refreshTokenClaims.getIssuedAt().toInstant().getEpochSecond();

        // Redis에 저장된 해당 사용자의 리프레시 토큰 업데이트
        String existingRefreshToken = redisUtil.getData("username" + username);
        if (existingRefreshToken != null) {
            redisUtil.deleteData("username" + username);
        }

        // 만료시간을 지정해 setDataExpire를 호출하면, 해당 기간이 지나면 자동으로 key-value가 삭제됩니다.
        redisUtil.setDataExpire("username" + username, jwt.getRefreshToken(), 604800000);

        // JSON 형태로 응답
        return jwt;
    }

    // 로그아웃
    public void logout(HttpServletRequest request) {
        // 1. access token 가져오기
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        // 2. access token에서 username 추출
        String username = jwtTokenUtils.parseClaims(accessToken).getSubject();
        log.info("access token에서 추출한 username : {}", username);

        // 3. Redis에서 해당 username 키를 조회
        String existingRefreshToken = redisUtil.getData("username" + username);

        // 4. 토큰이 존재하면 삭제, 없으면 예외 발생
        if (existingRefreshToken != null) {
            redisUtil.deleteData("username" + username);
            log.info("Redis에서 리프레시 토큰 삭제 완료");
        } else {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
    }

    public JwtDto reissue(HttpServletRequest request) {
        // 1. Request에서 Refresh Token 추출
        String refreshTokenValue = request.getHeader("Authorization").split(" ")[1];
        log.info("reissue - Authorization 헤더에서 추출한 refreshTokenValue={}", refreshTokenValue);
        // 2. Refresh Token에서 username 추출
        //    (refreshTokenClaims.getSubject()가 username이라고 가정)
        String username;
        try {
            Claims refreshTokenClaims = jwtTokenUtils.parseClaims(refreshTokenValue);
            username = refreshTokenClaims.getSubject();
        } catch (Exception e) {
            // 파싱 에러 → 잘못된 토큰
            log.error("reissue - parseClaims 실패, refreshTokenValue={}", refreshTokenValue, e);
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
        log.info("refresh token에서 추출한 username: {}", username);

        // 3. Redis에서 해당 username 키로 저장된 Refresh Token 가져오기
        String existingRefreshToken = redisUtil.getData("username" + username);
        log.info("reissue - Redis에서 가져온 existingRefreshToken={}", existingRefreshToken);
        // 토큰이 없거나, Redis에 저장된 값과 다르면 잘못된 토큰
        if (existingRefreshToken == null || !existingRefreshToken.equals(refreshTokenValue)) {
            log.error("reissue -토큰 불일치! existingRefreshToken={}, refreshTokenValue={}",
                    existingRefreshToken, refreshTokenValue);
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }

        // Refresh Token의 유효기간을 검증, 만료 시 예외 처리 로직

        // 4. username으로 사용자 정보 로드
        UserDetails userDetails = manager.loadUserByUsername(username);

        // 5. 새로운 Access Token, Refresh Token 생성
        JwtDto jwt = jwtTokenUtils.generateToken(userDetails);
        log.info("reissue: refresh token 재발급 완료");

        // 6. Redis에 저장된 기존 Refresh Token 삭제 후, 새 Refresh Token 저장
        redisUtil.deleteData("username" + username); // 기존 토큰 삭제

        // 유효 기간(초 단위) 계산
        Claims newRefreshTokenClaims = jwtTokenUtils.parseClaims(jwt.getRefreshToken());
        long validPeriod = newRefreshTokenClaims.getExpiration().toInstant().getEpochSecond()
                - newRefreshTokenClaims.getIssuedAt().toInstant().getEpochSecond();

        // 새 Refresh Token을 username 키로 Redis에 저장
        redisUtil.setDataExpire("username" + username, jwt.getRefreshToken(), validPeriod);
        log.info("새로운 Refresh Token 저장 (username: {}, 만료까지 {}초)", username, validPeriod);

        String storedToken = redisUtil.getData("username" + username);
        if (!jwt.getRefreshToken().equals(storedToken)) {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }

        return jwt;
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        String existingRefreshToken = redisUtil.getData("username" + username);
        if (existingRefreshToken != null) {
            redisUtil.deleteData("username" + username);
            log.info("Redis에서 리프레시 토큰 삭제 완료");
        }

        userRepository.delete(user);
        log.info("{} 회원 탈퇴 완료", username);
    }

    public String checkMemberByName(String username) {
        User user = findUserByUserName(username);
        if (user.getPhoneNumber() != null) return "wasUser";
        return "newUser";
    }
}
