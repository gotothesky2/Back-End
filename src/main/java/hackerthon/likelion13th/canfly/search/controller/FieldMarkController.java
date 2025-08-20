package hackerthon.likelion13th.canfly.search.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.service.UserService;
import hackerthon.likelion13th.canfly.search.dto.MajorDto;
import hackerthon.likelion13th.canfly.search.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "계열에 포함된 전공 목록",
            description = "계열(fieldId)에 속한 전공(major) 목록을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "FIELD_MAJOR_2007", description = "계열 기준 전공 목록 조회 완료")
    })
    @GetMapping("/majors")
    public ApiResponse<List<MajorDto>> getMajorsByField(
            @PathVariable Long fieldId
    ) {
        List<MajorDto> result = fieldService.getMajorsByField(fieldId);
        return ApiResponse.onSuccess(SuccessCode.FIELD_MAJOR_LIST_BY_FIELD_SUCCESS, result);
    }
}
