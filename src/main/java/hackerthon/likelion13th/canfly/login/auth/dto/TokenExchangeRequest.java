package hackerthon.likelion13th.canfly.login.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "authCode로 JWT와 디바이스 토큰을 교환하기 위한 요청")
public class TokenExchangeRequest {

    @Schema(description = "딥링크로 받은 임시 인증 코드", example = "ABCD1234")
    private String code;

}
