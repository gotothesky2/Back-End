package hackerthon.likelion13th.canfly.search.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import hackerthon.likelion13th.canfly.search.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "계열 북마크 관련 컨트롤러", description = "계열 북마크 컨트롤러입니다")
@RestController
@RequestMapping("/field/{fieldId}/like")
@RequiredArgsConstructor
public class FieldMarkController {
    private final UserService userService;
    private final FieldService fieldService;

    @Operation(summary = "계열 북마크", description = "계열에 북마크를 추가하거나 취소하는 api.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FIELD_MARK_2001", description = "계열 북마크 성공")
    })
    @PostMapping("/toggle")
    public ApiResponse<Boolean> toggleLike(
            @PathVariable(name = "fieldId") Long fieldId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByProviderId(customUserDetails.getUsername());
        fieldService.toggleFieldLike(fieldId, user);

        return ApiResponse.onSuccess(SuccessCode.FIELD_LIKE_SUCCESS, true);
    }
}
