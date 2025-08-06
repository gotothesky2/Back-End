package hackerthon.likelion13th.canfly.grades.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.service.MockService;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
            @ApiResponse(responseCode = "Mock_2011", description = "모의고사 등록이 완료되었습니다."),
    })
    public hackerthon.likelion13th.canfly.global.api.ApiResponse<MockResponseDto> createMock(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MockRequestDto mockRequestDto) {

        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        MockResponseDto dto = mockService.createMock(user.getUid(), mockRequestDto);

        return hackerthon.likelion13th.canfly.global.api.ApiResponse.onSuccess(SuccessCode.MOCK_CREATE_SUCCESS, dto);
    }

    @PostMapping("/{mockId}")
    @Operation(summary = "모의고사 점수 등록", description = "어떤 모의고사의 특정 과목 성적을 입력하는 메서드입니다..")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2012", description = "모의고사 성적 등록이 완료되었습니다."),
    })
    public hackerthon.likelion13th.canfly.global.api.ApiResponse<MockResponseDto> createMockScoreLists(
            @PathVariable Long mockId,
            @RequestBody MockRequestDto.MockScoreRequestDto mockScoreRequestDto
            ) {

        MockResponseDto responseDto = mockService.addMockScoreToMock(mockId, mockScoreRequestDto);
        return hackerthon.likelion13th.canfly.global.api.ApiResponse.onSuccess(SuccessCode.MOCKSCORE_CREATE_SUCCESS, responseDto);
    }

    @GetMapping
    @Operation(summary = "전체 모의고사 조회", description = "사용자가 진행했던 모든 모의고사를 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2001", description = "전체 모의고사 조회가 완료되었습니다."),
    })
    public ResponseEntity<List<MockResponseDto>> getAllMocksOfUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        /* providerId 기반으로 User 찾기 */
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        List<MockResponseDto> allMocks = mockService.getAllMocksByUserId(user.getUid());

        return ResponseEntity.ok(allMocks);
    }

    @GetMapping("/{mockId}")
    @Operation(summary = "특정 모의고사 조회", description = "하나의 모의고사를 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2002", description = "모의고사 조회가 완료되었습니다."),
    })
    public ResponseEntity<MockResponseDto> getMockById(@PathVariable Long mockId) {
        MockResponseDto responseDto = mockService.getMockById(mockId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{mockId}/{mockScoreId}")
    @Operation(summary = "모의고사 내 특정 과목 성적 조회", description = "특정 과목의 성적을 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2003", description = "과목 성적 조회가 완료되었습니다."),
    })
    public ResponseEntity<MockResponseDto.MockScoreResponseDto> getMockScore(@PathVariable Long mockId, @PathVariable Long mockScoreId) {
        MockResponseDto.MockScoreResponseDto mockScore = mockService.getMockScoreById(mockId, mockScoreId);
        return ResponseEntity.ok(mockScore);
    }

    @PutMapping("/{mockId}")
    @Operation(summary = "모의고사 정보 수정", description = "특정 모의고사의 정보를 수정하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2013", description = "특정 모의고사 정보 수정이 완료되었습니다."),
    })
    public ResponseEntity<MockResponseDto> updateMock(@PathVariable Long mockId, @RequestBody MockRequestDto mockRequestDto) {
        MockResponseDto responseDto = mockService.updateMock(mockId, mockRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{mockId}/{mockScoreId}")
    @Operation(summary = "모의고사 성적 수정", description = "특정 모의고사의 성적을 수정하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2014", description = "모의고사 성적 수정이 완료되었습니다."),
    })
    public ResponseEntity<MockResponseDto.MockScoreResponseDto> updateMockScore(@PathVariable Long mockId, @PathVariable Long mockScoreId, @RequestBody MockRequestDto.MockScoreRequestDto mockScoreRequestDto) {
        MockResponseDto.MockScoreResponseDto responseDto = mockService.updateMockScore(mockId, mockScoreRequestDto);
        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/{mockId}")
    @Operation(summary = "모의고사 삭제", description = "특정 모의고사를 삭제하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_2004", description = "모의고사 삭제가 완료되었습니다."),
    })
    public hackerthon.likelion13th.canfly.global.api.ApiResponse<Boolean> deleteMock(@PathVariable Long mockId) {
        mockService.deleteMock(mockId);
        return hackerthon.likelion13th.canfly.global.api.ApiResponse.onSuccess(SuccessCode.MOCK_DELETE_SUCCESS, true);
    }

}
