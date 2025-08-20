package hackerthon.likelion13th.canfly.search.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import hackerthon.likelion13th.canfly.search.dto.MajorDto;
import hackerthon.likelion13th.canfly.search.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "전공 관련 컨트롤러", description = "전공 관련 API")
@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController {
    private final UserService userService;
    private final MajorService majorService;

    @Operation(summary = "내가 좋아요한 전공 전체", description = "로그인 사용자가 북마크한 모든 전공을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "MAJOR_2006", description = "내 북마크 전공 전체 조회 완료")
    })
    @GetMapping("/like")
    public ApiResponse<List<MajorDto>> getMyLikedMajors(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        List<MajorDto> majors = majorService.getAllLikedMajors(user);

        return ApiResponse.onSuccess(SuccessCode.MAJOR_LIKED_LIST_VIEW_SUCCESS, majors);
    }

    @Operation(summary = "전공 전체", description = "모든 전공을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "MAJOR_2007", description = " 전체 조회 완료")
    })
    @GetMapping("/all")
    public ApiResponse<List<MajorDto>> getMajors(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<MajorDto> majors = majorService.getAllMajors();

        return ApiResponse.onSuccess(SuccessCode.MAJOR_LIST_VIEW_SUCCESS, majors);
    }
}
