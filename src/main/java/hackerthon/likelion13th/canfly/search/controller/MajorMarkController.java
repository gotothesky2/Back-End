package hackerthon.likelion13th.canfly.search.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import hackerthon.likelion13th.canfly.search.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "전공 및 전공-대학 북마크", description = "전공 북마크 컨트롤러입니다")
@RestController
@RequestMapping("/major/{majorId}/like")
@RequiredArgsConstructor
public class MajorMarkController {
    private final UserService userService;
    private final MajorService majorService;

    @Operation(summary = "전공 북마크", description = "전공에 북마크를 추가하거나 취소하는 api.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MAJOR_MARK_2001", description = "계열 북마크 성공")
    })
    @PostMapping("/toggle")
    public ApiResponse<Boolean> toggleLike(
            @PathVariable(name = "majorId") Long majorId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        majorService.toggleMajorLike(majorId, user);

        return ApiResponse.onSuccess(SuccessCode.MAJOR_LIKE_SUCCESS, true);
    }

    @Operation(summary = "전공+대학 북마크 토글", description = "전공/대학 조합 북마크를 토글합니다.")
    @PostMapping("/univ/{univId}/toggle")
    public ApiResponse<Boolean> toggleLikeWithUniv(
            @PathVariable Long majorId,
            @PathVariable Long univId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        majorService.toggleMajorUnivLike(majorId, univId, user);
        return ApiResponse.onSuccess(SuccessCode.MAJOR_UNIV_LIKE_SUCCESS, true);
    }
}
