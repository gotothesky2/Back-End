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
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "USER_2006", "프로필 저장이 완료되었습니다."),
    USER_INFO_GET_SUCCESS(HttpStatus.OK, "USER_2007", "유저 정보 조회가 완료되었습니다."),

    MOCK_CREATE_SUCCESS(HttpStatus.CREATED, "MOCK_2011", "모의고사 등록이 완료되었습니다."),
    MOCKSCORE_CREATE_SUCCESS(HttpStatus.CREATED, "MOCKSCORE_2011", "모의고사 성적 등록이 완료되었습니다."),
    MOCK_PUT_SUCCESS(HttpStatus.OK, "MOCK_2012", "모의고사 정보 수정이 완료되었습니다.."),
    MOCKSCORE_PUT_SUCCESS(HttpStatus.OK, "MOCKSCORE_2012", "모의고사 성적 수정이 완료되었습니다.."),
    MOCK_GET_SUCCESS(HttpStatus.OK, "MOCK_2001", "모의고사 조회가 완료되었습니다."),
    MOCK_GET_ALL_SUCCESS(HttpStatus.OK, "MOCK_2002", "모의고사 전체 조회가 완료되었습니다."),
    MOCKSCORE_GET_SUCCESS(HttpStatus.OK, "MOCKSCORE_2001", "모의고사 성적 조회가 완료되었습니다."),
    MOCK_DELETE_SUCCESS(HttpStatus.OK, "MOCK_2004", "모의고사 삭제가 완료되었습니다."),

    REPORT_CREATE_SUCCESS(HttpStatus.OK, "REPORT_2011", "내신 등록이 완료되었습니다."),
    REPORTSCORE_CREATE_SUCCESS(HttpStatus.OK, "REPORTSCORE_2011", "내신 성적 등록이 완료되었습니다"),
    REPORT_PUT_SUCCESS(HttpStatus.OK, "REPORT_2012", "내신 수정이 완료되었습니다."),
    REPORTSCORE_PUT_SUCCESS(HttpStatus.OK, "REPORTSCORE_2012", "내신 성적 수정이 완료되었습니다."),
    REPORT_GET_SUCCESS(HttpStatus.OK, "REPORT_2001", "내신 정보 조회가 완료되었습니다.."),
    REPORT_GET_ALL_SUCCESS(HttpStatus.OK, "REPORT_2002", "전체 내신 정보 조회가 완료되었습니다.."),
    REPORTSCORE_GET_SUCCESS(HttpStatus.OK, "REPORTSCORE_2001", "내신 점수 조회가 완료되었습니다."),
    REPORT_DELETE_SUCCESS(HttpStatus.OK, "REPORT_2004", "내신 삭제가 완료되었습니다.."),

    // 형이 추가한 코드
    MAJORSYNC_BULK_SUCCESS(HttpStatus.OK, "Sync_2012", "DB 동기화가 완료되었습니다."),
    FIELD_LIKE_SUCCESS(HttpStatus.OK, "FIELD_MARK_2001", "계열 북마크 성공"),
    MAJOR_LIKE_SUCCESS(HttpStatus.OK, "MAJOR_MARK_2001", "전공 북마크 성공"),
    MAJOR_UNIV_LIKE_SUCCESS(HttpStatus.OK, "MAJOR_UNIV_MARK_2001", "전공+대학 북마크 성공"),
    FIELD_LIKED_LIST_VIEW_SUCCESS(HttpStatus.OK, "FIELD_2006", "로그인 사용자가 북마크한 모든 계열명을 반환합니다."),
    FIELD_LIST_VIEW_SUCCESS(HttpStatus.OK, "FIELD_2007", "계열 전체 조회 완료."),
    MAJOR_LIKED_LIST_VIEW_SUCCESS(HttpStatus.OK, "MAJOR_2006", "내 북마크 전공 전체 조회 완료"),
    MAJOR_LIST_VIEW_SUCCESS(HttpStatus.OK, "MAJOR_2007", "전공 전체 조회 완료."),
    MAJOR_UNIV_LIKED_LIST_VIEW_SUCCESS(HttpStatus.OK, "MAJOR_UNIV_2006", "전공별 대학-전공 북마크 조회 완료"),
    MAJOR_UNIV_LIST_BY_MAJOR_SUCCESS(HttpStatus.OK, "MAJOR_UNIV_2007", "전공 기준 대학 목록 조회 완료"),
    FIELD_MAJOR_LIST_BY_FIELD_SUCCESS(HttpStatus.OK, "FIELD_MAJOR_2007", "계열 기준 전공 목록 조회 완료"),
    MAJOR_UNIV_BOOKMARK_LIST_SUCCESS(HttpStatus.OK, "UNIV_MAJOR_2008", "전공+대학 북마크 목록 조회 완료"),

    // 내가 추가한 코드
    TOKEN_PROCESS_SUCCESS(HttpStatus.OK, "TOKEN_2001", "코인(토큰) 사용이 완료되었습니다.");

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
