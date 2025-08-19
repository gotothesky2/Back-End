package hackerthon.likelion13th.canfly.grades.controller;

import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.grades.dto.MockCreateRequest;
import hackerthon.likelion13th.canfly.grades.dto.MockCreateResponse;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.service.MockService;
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

@Tag(name="모의고사", description = "모의고사 성적 입력 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/grades/mock")
@Slf4j
public class MockController {

    private final MockService mockService;
    private final UserService userService;


    @PostMapping
    @Operation(summary = "모의고사 등록", description = "특정 년도, 월에 진행한 특정 학년의 모의고사를 등록하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mock_2011", description = "모의고사 등록이 완료되었습니다."),
    })
    public ApiResponse<MockResponseDto> createMock(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MockRequestDto mockRequestDto) {

        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        MockResponseDto dto = mockService.createMock(user.getUid(), mockRequestDto);

        return ApiResponse.onSuccess(SuccessCode.MOCK_CREATE_SUCCESS, dto);

    }

    @GetMapping
    @Operation(summary = "전체 모의고사 조회", description = "사용자가 진행했던 모든 모의고사를 조회하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mock_2001", description = "전체 모의고사 조회가 완료되었습니다."),
    })

    public ApiResponse<List<MockResponseDto>> getAllMocksOfUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        /* providerId 기반으로 User 찾기 */
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        List<MockResponseDto> allMocks = mockService.getAllMocksByUserId(user.getUid());

        return ApiResponse.onSuccess(SuccessCode.MOCK_GET_ALL_SUCCESS, allMocks);

    }

    @GetMapping("/{mockId}")
    @Operation(summary = "특정 모의고사 조회", description = "하나의 모의고사를 조회하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mock_2002", description = "모의고사 조회가 완료되었습니다."),
    })
    public ApiResponse<MockResponseDto> getMockById(@PathVariable Long mockId) {
        MockResponseDto responseDto = mockService.getMockById(mockId);
        return ApiResponse.onSuccess(SuccessCode.MOCK_GET_SUCCESS, responseDto);
    }

    @GetMapping("/{mockId}/{mockScoreId}")
    @Operation(summary = "모의고사 내 특정 과목 성적 조회", description = "특정 과목의 성적을 조회하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mockscore_2003", description = "과목 성적 조회가 완료되었습니다."),
    })
    public ApiResponse<MockResponseDto.MockScoreResponseDto> getMockScore(@PathVariable Long mockId, @PathVariable Long mockScoreId) {
        MockResponseDto.MockScoreResponseDto mockScore = mockService.getMockScoreById(mockId, mockScoreId);
        return ApiResponse.onSuccess(SuccessCode.MOCKSCORE_GET_SUCCESS, mockScore);
    }

// 뭔가 만들면서 이상해져서 수정할 건데, 일단 이걸로 POST+DELETE를 해야 함
//    @Operation(summary = "모의고사 성적 수정", description = "특정 모의고사의 성적을 수정하는 메서드입니다.")
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mock_2013", description = "특정 모의고사 정보 수정이 완료되었습니다."),
//    })
//    public ApiResponse<MockResponseDto> updateMockScore(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long mockId, @RequestBody MockRequestDto mockRequestDto) {
//        User user = userService.findUserByProviderId(userDetails.getUsername());
//        Mock updatedMock = new Mock();
//        if (mockRequestDto.getScoreLists() != null && !mockRequestDto.getScoreLists().isEmpty()) {
//            List<MockRequestDto.MockScoreRequestDto> newScores = mockRequestDto.getScoreLists();
//            for (MockRequestDto.MockScoreRequestDto scoreDto : newScores) {
//                MockScore mockScore = MockScore.builder()
//                        .standardScore(scoreDto.getStandardScore())
//                        .percentile(scoreDto.getPercentile())
//                        .grade(scoreDto.getGrade())
//                        .cumulative(scoreDto.getCumulative())
//                        .category(scoreDto.getCategory())
//                        .name(scoreDto.getName())
//                        .build();
//                updatedMock.addMockScore(mockScore);
//            }
//        }
//        MockResponseDto responseDto = new MockResponseDto(updatedMock);
//        return ApiResponse.onSuccess(SuccessCode.MOCKSCORE_PUT_SUCCESS, responseDto);
//    }


    @DeleteMapping("/{mockId}")
    @Operation(summary = "모의고사 삭제", description = "특정 모의고사를 삭제하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mock_2004", description = "모의고사 삭제가 완료되었습니다."),
    })
    public ApiResponse<Boolean> deleteMock(@PathVariable Long mockId) {
        mockService.deleteMock(mockId);
        return ApiResponse.onSuccess(SuccessCode.MOCK_DELETE_SUCCESS, true);
    }

    // GPT
    @PostMapping("/excel")
    @Operation(
            summary = "모의고사 등록(엑셀 평가)",
            description = "엑셀 수식으로 각 과목의 백분위/등급/누적(%)를 계산한 뒤 저장합니다. " +
                    "규칙: 수학 3택1, (과탐/사탐) 합산 최대 2, 제2외국어 최대 1. " +
                    "표준점수 과목에서 계산 결과가 (백분위=0, 등급=0, 누적=0)이면 입력 오류로 처리됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Mock_2011", description = "모의고사 등록이 완료되었습니다."),
    })
    public ApiResponse<MockCreateResponse> createMockByExcel(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MockCreateRequest req
    ) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());

        MockCreateResponse res = mockService.createMockByExcel(user.getUid(), req);

        return ApiResponse.onSuccess(SuccessCode.MOCK_CREATE_SUCCESS, res);
    }
}
