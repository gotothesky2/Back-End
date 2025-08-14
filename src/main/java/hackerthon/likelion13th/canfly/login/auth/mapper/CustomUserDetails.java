package hackerthon.likelion13th.canfly.login.auth.mapper;

import hackerthon.likelion13th.canfly.domain.user.Address;
import hackerthon.likelion13th.canfly.domain.user.OAuth;
import hackerthon.likelion13th.canfly.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

// spring security에서의 사용자 인증 정보
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {
    private String id;
    private String providerId;
    private String username;
    private String nickname;
    private Address address;
    private String email;
    private String provider;
    private String accessToken;
    private LocalDateTime expireDate;


    public static CustomUserDetails fromEntity(User u, OAuth o) {
        return CustomUserDetails.builder()
                .username(o.getProviderUserId())
                .nickname(u.getName())
                .id(u.getUid())
                .email(u.getEmail())
                .provider(o.getProvider())
                .accessToken(o.getAccessToken())
                .expireDate(o.getExpireDate())
                .build();
    }

    // 권한 부여
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }

    // 비밀번호 반환
    // 자체 로그인이 있다면 필요하지만 소셜 로그인만 취급중이므로 고정값 설정
    @Override
    public String getPassword() {
        return "Password";
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User toEntity() {
        return User.builder()
                .name(this.nickname)
                .email(this.email)
                .build();
    }


    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "providerId=" + username +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}