package hackerthon.likelion13th.canfly.login.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.dto.JwtDto;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.dto.CoinRequestDto;
import hackerthon.likelion13th.canfly.login.dto.CoinResponseDto;
import hackerthon.likelion13th.canfly.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    // private final AmazonS3Manager amazonS3Manager;

    @Operation(summary = "로그아웃", description = "로그아웃하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2001", description = "로그아웃 되었습니다."),
    })
    @DeleteMapping("/logout")
    public ApiResponse<Integer> logout(HttpServletRequest request) {
        // acess token 강제 만료
        userService.logout(request);
        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, 1);
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2002", description = "토큰 재발급이 완료되었습니다."),
    })
    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(
            HttpServletRequest request
    ) {
        JwtDto jwt = userService.reissue(request);
        return ApiResponse.onSuccess(SuccessCode.USER_REISSUE_SUCCESS, jwt);
    }

    @Operation(summary = "회원탈퇴", description = "회원 탈퇴하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2003", description = "회원탈퇴가 완료되었습니다."),
    })
    @DeleteMapping("/me")
    public ApiResponse<Integer> deleteUser(Authentication auth) {
        userService.deleteUser(auth.getName());
        return ApiResponse.onSuccess(SuccessCode.USER_DELETE_SUCCESS, 1);
    }

    @Operation(summary = "토큰 사용 및 충전", description = "amount가 0이면 토큰 1개 사용, 0보다 크면 해당 양만큼 충전합니다.")
    @PatchMapping("/token")
    public ApiResponse<CoinResponseDto> updateUserCoins(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 또는 Authentication auth 객체
            @RequestBody CoinRequestDto coinRequestDto) {
        int amount = coinRequestDto.getAmount(); //내가 볼 때 그냥 token을 다른 테이블에 추가시키는 게 나음 ㅅ;ㅂ 이거 너무 많아 정보가
        String username = userDetails.getUsername();
        User updatedUser = userService.processCoins(username, amount);
        CoinResponseDto responseDTO = CoinResponseDto.fromEntity(updatedUser);

        // 3. 최종적으로 변환된 DTO를 클라이언트에게 전달합니다.
        return ApiResponse.onSuccess(SuccessCode.TOKEN_PROCESS_SUCCESS, responseDTO);
    }
    /*
    @Operation(summary = "프로필 사진 첨부", description = "프로필 사진을 첨부하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2004", description = "프로필 사진 첨부가 완료되었습니다.")
    })
    @PostMapping(value = "/img/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Boolean> createProfileImage(
            @RequestPart(value = "profile") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws IOException {
        String dirName = "profile/";
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        userService.createProfileImage(dirName, file, user);

        return ApiResponse.onSuccess(SuccessCode.USER_PROFILE_IMAGE_UPDATED, true);
    }

    @Operation(summary = "사진 삭제", description = "s3에 업로드 된 사진을 삭제하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FILE_2001", description = "사진 삭제가 완료되었습니다.")
    })
    @DeleteMapping(value = "/img/delete")
    public ApiResponse<Boolean> deleteImage(
            @RequestParam("filePath") String filePath,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        amazonS3Manager.deleteFile(filePath);

        return ApiResponse.onSuccess(SuccessCode.FILE_DELETE_SUCCESS, true);
    }
    */
}
