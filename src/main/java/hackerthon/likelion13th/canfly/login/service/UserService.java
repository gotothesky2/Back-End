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
import hackerthon.likelion13th.canfly.login.repository.OAuthRepository;
import hackerthon.likelion13th.canfly.login.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JpaUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtil redisUtil;
    private final OAuthRepository oAuthRepository;

    private static final String RT_PREFIX = "providerId:";
    // private final AmazonS3Manager amazonS3Manager;

    // 로그인

    // username으로 User찾기
    public User findUserByProviderId(String providerId) {
        return oAuthRepository.findByProviderUserId(providerId)   // ★ 새 메서드
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND))
                .getUser();
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

    /* ========== JWT 발급 & 저장 ========== */
    public JwtDto jwtMakeSave(String providerId) {
        UserDetails details = manager.loadUserByUsername(providerId);   // subject = providerId
        JwtDto jwt = jwtTokenUtils.generateToken(details);

        long ttlSec = (jwtTokenUtils.parseClaims(jwt.getRefreshToken())
                .getExpiration().getTime() - System.currentTimeMillis()) / 1000;

        String redisKey = RT_PREFIX + providerId;
        redisUtil.deleteData(redisKey);
        redisUtil.setDataExpire(redisKey, jwt.getRefreshToken(), ttlSec);
        return jwt;
    }



    /* ───────── 로그아웃 ───────── */
    public void logout(HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization").split(" ")[1];
        String providerId  = jwtTokenUtils.parseClaims(accessToken).getSubject(); // providerId

        String redisKey = RT_PREFIX + providerId;
        String saved = redisUtil.getData(redisKey);
        if (saved != null) {
            redisUtil.deleteData(redisKey);
        } else {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
    }

    /* ========== Refresh-Token 재발급 ========== */
    public JwtDto reissue(HttpServletRequest req) {

        String oldRt = req.getHeader("Authorization").split(" ")[1];
        String providerId;
        try {
            providerId = jwtTokenUtils.parseClaims(oldRt).getSubject();
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }

        String redisKey = RT_PREFIX + providerId;
        String savedRt  = redisUtil.getData(redisKey);
        if (savedRt == null || !savedRt.equals(oldRt))
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);

        UserDetails details = manager.loadUserByUsername(providerId);
        JwtDto jwt = jwtTokenUtils.generateToken(details);

        long ttlSec = (jwtTokenUtils.parseClaims(jwt.getRefreshToken())
                .getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        redisUtil.setDataExpire(redisKey, jwt.getRefreshToken(), ttlSec);
        return jwt;
    }

    /* ========== 회원 탈퇴 ========== */
    @Transactional
    public void deleteUser(String providerId) {

        User user = findUserByProviderId(providerId);   // ← 변경 핵심

        // Redis RT 삭제
        String redisKey = RT_PREFIX + providerId;
        redisUtil.deleteData(redisKey);

        // User 삭제 (OAuth 는 FK cascade 로 같이 삭제)
        userRepository.delete(user);
    }

    public String checkMemberByName(String username) {
        User user = findUserByUserName(username);
        if (user.getPhoneNumber() != null) return "wasUser";
        return "newUser";
    }

    /* ===== 기타 기존 메서드 중 닉네임으로 찾는 경우만 Name 유지 ===== */
    public User findUserByUserName(String nickName) {
        return userRepository.findByName(nickName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNAME));
    }
}
