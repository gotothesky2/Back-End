package hackerthon.likelion13th.canfly.search.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import hackerthon.likelion13th.canfly.search.dto.FieldDto;
import hackerthon.likelion13th.canfly.search.repository.FieldBookmarkRepository;
import hackerthon.likelion13th.canfly.search.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "계열 조회", description = "계열 관련 API")
@RestController
@RequestMapping("/field")
@RequiredArgsConstructor
public class FieldController {
    private final UserService userService;
    private final FieldService fieldService;
    private final FieldBookmarkRepository fieldBookmarkRepository;

    @Operation(summary = "내가 좋아요한 계열 전체", description = "로그인 사용자가 북마크한 모든 계열을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "FIELD_2006", description = "내 북마크 계열 전체 조회 완료")
    })
    @GetMapping("/like")
    public ApiResponse<List<FieldDto>> getMyLikedFields(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        List<FieldDto> fields = fieldService.getAllLikedFields(user);

        return ApiResponse.onSuccess(SuccessCode.FIELD_LIKED_LIST_VIEW_SUCCESS, fields);
    }

    @Operation(summary = "계열 전체", description = "모든 계열을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "FIELD_2007", description = "계열 전체 조회 완료")
    })
    @GetMapping("/all")
    public ApiResponse<List<FieldDto>> getFields(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<FieldDto> fields = fieldService.getAllFields();

        return ApiResponse.onSuccess(SuccessCode.FIELD_LIST_VIEW_SUCCESS, fields);
    }
}
