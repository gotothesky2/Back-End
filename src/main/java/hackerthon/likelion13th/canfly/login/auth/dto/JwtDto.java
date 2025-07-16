package hackerthon.likelion13th.canfly.login.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class JwtDto {
    private String accessToken;
    private String refreshToken;
    private String signIn;
}
