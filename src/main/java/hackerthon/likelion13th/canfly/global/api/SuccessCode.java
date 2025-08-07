package hackerthon.likelion13th.canfly.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode { // 성공
    OK(HttpStatus.OK, "COMMON_200", "Success"),
    CREATED(HttpStatus.CREATED, "COMMON_201", "Created"),

    USER_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2011", "회원가입& 로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2001", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_2002", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_2003", "회원탈퇴가 완료되었습니다."),

    MOCK_CREATE_SUCCESS(HttpStatus.OK, "MOCK_2011", "모의고사 등록이 완료되었습니다."),
    MOCKSCORE_CREATE_SUCCESS(HttpStatus.OK, "MOCKSCORE_2012", "모의고사 성적 등록이 완료되었습니다."),
    MOCK_DELETE_SUCCESS(HttpStatus.OK, "MOCK_2004", "모의고사 삭제가 완료되었습니다."),

    REPORT_CREATE_SUCCESS(HttpStatus.OK, "REPORT_2011", "내신 등록이 완료되었습니다."),
    REPORTSCORE_CREATE_SUCCESS(HttpStatus.OK, "REPORTSCORE_2012", "내신 성적 등록이 완료되었습니다"),
    REPORT_DELETE_SUCCESS(HttpStatus.OK, "REPORT_2004", "내신 삭제가 완료되었습니다..");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    // 응답 코드 상세 정보 return
    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
