package hackerthon.likelion13th.canfly.grades.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.service.MockService;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name="모의고사", description = "모의고사 성적 입력 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/grades/mock")
@Slf4j
public class MockController {

    private final MockService mockService;
    private final UserService userService;


    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "Mock_200", description = "모의고사 등록이 완료되었습니다."),
    })
    public hackerthon.likelion13th.canfly.global.api.ApiResponse<MockResponseDto> createMock(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MockRequestDto mockRequestDto) {


        User user = userService.findUserByUserName(customUserDetails.getUsername());
        String userId = user.getUid();

        MockResponseDto responseDto = mockService.createMock(userId, mockRequestDto);
        return hackerthon.likelion13th.canfly.global.api.ApiResponse.onSuccess(SuccessCode.MOCK_CREATE_SUCCESS, responseDto);
    }

    @GetMapping("/{mockId}")
    public ResponseEntity<MockResponseDto> getMockById(@PathVariable Long mockId) {
        MockResponseDto responseDto = mockService.getMockById(mockId);
        return ResponseEntity.ok(responseDto);
    }

}
