package hackerthon.likelion13th.canfly.grades.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.grades.dto.ReportRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.ReportResponseDto;
import hackerthon.likelion13th.canfly.grades.service.ReportService;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="내신", description = "내신 성적 입력 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/grades/report")
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;


    @PostMapping
    @Operation(summary = "내신 등록", description = "특정 년도, 월에 진행한 특정 학년의 내신를 등록하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2011", description = "내신 등록이 완료되었습니다."),
    })
    public ApiResponse<ReportResponseDto> createReport(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ReportRequestDto reportRequestDto) {


        User user = userService.findUserByProviderId(customUserDetails.getUsername());

        ReportResponseDto responseDto = reportService.createReport(user.getUid(), reportRequestDto);
        return ApiResponse.onSuccess(SuccessCode.REPORT_CREATE_SUCCESS, responseDto);
    }

    @PostMapping("/{reportId}")
    @Operation(summary = "내신 점수 등록", description = "어떤 내신의 특정 과목 성적을 입력하는 메서드입니다..")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2012", description = "내신 성적 등록이 완료되었습니다."),
    })
    public ApiResponse<ReportResponseDto> createReportScoreLists(
            @PathVariable Long reportId,
            @RequestBody ReportRequestDto.ReportScoreRequestDto reportScoreRequestDto
    ) {

        ReportResponseDto responseDto = reportService.addReportScoreToReport(reportId, reportScoreRequestDto);
        return ApiResponse.onSuccess(SuccessCode.REPORTSCORE_CREATE_SUCCESS, responseDto);
    }

    @GetMapping
    @Operation(summary = "전체 내신 조회", description = "사용자가 진행했던 모든 내신를 조회하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2001", description = "전체 내신 조회가 완료되었습니다."),
    })
    public ApiResponse<List<ReportResponseDto>> getAllReportsOfUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        List<ReportResponseDto> allReports = reportService.getAllReportsByUserId(user.getUid());
        return ApiResponse.onSuccess(SuccessCode.REPORT_GET_ALL_SUCCESS, allReports);
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "특정 내신 조회", description = "하나의 내신를 조회하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2002", description = "내신 조회가 완료되었습니다."),
    })
    public ApiResponse<ReportResponseDto> getReportById(@PathVariable Long reportId) {
        ReportResponseDto responseDto = reportService.getReportById(reportId);
        return ApiResponse.onSuccess(SuccessCode.REPORT_GET_SUCCESS, responseDto);
    }

    @GetMapping("/{reportId}/{reportScoreId}")
    @Operation(summary = "내신 내 특정 과목 성적 조회", description = "특정 과목의 성적을 조회하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2003", description = "과목 성적 조회가 완료되었습니다."),
    })
    public ApiResponse<ReportResponseDto.ReportScoreResponseDto> getReportScore(@PathVariable Long reportId, @PathVariable Long reportScoreId) {
        ReportResponseDto.ReportScoreResponseDto reportScore = reportService.getReportScoreById(reportId, reportScoreId);
        return ApiResponse.onSuccess(SuccessCode.REPORTSCORE_GET_SUCCESS, reportScore);
    }

    @PutMapping("/{reportId}")
    @Operation(summary = "내신 정보 수정", description = "특정 내신의 정보를 수정하는 메서드입니다.") //※ 규칙: 여기에서 절대로 CategoryName은 건들면 안됨
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2013", description = "특정 내신 정보 수정이 완료되었습니다."),
    })
    public ApiResponse<ReportResponseDto> updateReport(@PathVariable Long reportId, @RequestBody ReportRequestDto reportRequestDto) {
        ReportResponseDto responseDto = reportService.updateReport(reportId, reportRequestDto);
        return ApiResponse.onSuccess(SuccessCode.REPORT_PUT_SUCCESS, responseDto);
    }

    @PutMapping("/{reportId}/{reportScoreId}")
    @Operation(summary = "내신 성적 수정", description = "특정 내신의 성적을 수정하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2014", description = "내신 성적 수정이 완료되었습니다."),
    })
    public ApiResponse<ReportResponseDto.ReportScoreResponseDto> updateReportScore(@PathVariable Long reportId, @PathVariable Long reportScoreId, @RequestBody ReportRequestDto.ReportScoreRequestDto reportScoreRequestDto) {
        ReportResponseDto.ReportScoreResponseDto responseDto = reportService.updateReportScore(reportScoreId, reportScoreRequestDto);
        return ApiResponse.onSuccess(SuccessCode.REPORTSCORE_PUT_SUCCESS, responseDto);
    }


    @DeleteMapping("/{reportId}")
    @Operation(summary = "내신 삭제", description = "특정 내신를 삭제하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Report_2004", description = "내신 삭제가 완료되었습니다."),
    })
    public ApiResponse<Boolean> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ApiResponse.onSuccess(SuccessCode.REPORT_DELETE_SUCCESS, true);
    }

}
