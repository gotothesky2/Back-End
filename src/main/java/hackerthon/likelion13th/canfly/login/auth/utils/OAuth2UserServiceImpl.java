package hackerthon.likelion13th.canfly.login.auth.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// OAuth2 인증 성공 후 사용자 데이터를 조회&처리
@Slf4j
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // DefaultOAuth2UserService -> 사용자 정보 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 클라이언트 등록 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 사용자 데이터 처리
        Map<String, Object> attributes = handleProviderAttributes(registrationId, oAuth2User);
        // token, ttl 처리
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String refreshToken = null;
        Map<String, Object> additionalParams = userRequest.getAdditionalParameters();
        if (additionalParams.containsKey("refresh_token")) {
            refreshToken = (String) additionalParams.get("refresh_token");
        }
        Instant expiresAtInstant = userRequest.getAccessToken().getExpiresAt();
        LocalDateTime expiresAt = null;
        if (expiresAtInstant != null) {
            expiresAt = LocalDateTime.ofInstant(expiresAtInstant, ZoneId.systemDefault());
        }
        attributes.put("oauth2AccessToken", accessToken);
        attributes.put("oauth2RefreshToken", refreshToken);
        attributes.put("oauth2ExpiresAt", expiresAt);
        // OAuth2User 객체 생성
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")), // 기본 권한 설정
                attributes, // 사용자 속성
                (String) attributes.get("nameAttribute") // 주요 식별 속성
        );
    }

    //  사용자 데이터를 처리
    private Map<String, Object> handleProviderAttributes(String registrationId, OAuth2User oAuth2User) {
        Map<String, Object> attributes = new HashMap<>();
        String nameAttribute = "";
        switch (registrationId) {
            case "kakao": { // Kakao
                attributes.put("provider", "kakao");
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                Long kakaoId = oAuth2User.getAttribute("id");
                String kakaoIdStr = String.valueOf(kakaoId);
                attributes.put("id", kakaoIdStr);
                attributes.put("email", kakaoAccount.get("email"));
                attributes.put("name", profile.get("nickname"));
                nameAttribute = "id";
                break;
            }
            default:
                throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }
        attributes.put("nameAttribute", nameAttribute);
        log.info("Attributes processed for provider '{}': {}", registrationId, attributes);
        return attributes;
    }
}