package hackerthon.likelion13th.canfly.global.api;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// API 응답 상세 정보
@Getter
@Builder
public class ReasonDTO implements BaseCode {

    private HttpStatus httpStatus; // HTTP 상태 코드
    private String code; // 응답 코드
    private String message; // 응답 메시지

    // 인터페이스 구현
    @Override
    public ReasonDTO getReason() {
        return this;
    }
}