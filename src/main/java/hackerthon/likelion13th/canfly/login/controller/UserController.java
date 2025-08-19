package hackerthon.likelion13th.canfly.login.controller;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.login.auth.dto.JwtDto;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.dto.*;
import hackerthon.likelion13th.canfly.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "토큰 사용 및 충전", description = "amount만큼 보유 토큰 수량에 더합니다. (음수 가능, 0 불가)")
    @PatchMapping("/token")
    public ApiResponse<CoinResponseDto> updateUserCoins(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 또는 Authentication auth 객체
            @RequestBody CoinRequestDto coinRequestDto) {
        int amount = coinRequestDto.getAmount();
        String userPId = userDetails.getUsername();
        User updatedUser = userService.processCoins(userPId, amount);
        CoinResponseDto responseDTO = CoinResponseDto.fromEntity(updatedUser);
        return ApiResponse.onSuccess(SuccessCode.TOKEN_PROCESS_SUCCESS, responseDTO);
    }

    @Operation(summary = "프로필 수정", description = "팝업에서 입력한 전화번호/성별/학교/학년을 저장합니다 -> 성별 (MAN/WOMAN), 학년 (1, 2, 3)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER_2006", description = "프로필 저장이 완료되었습니다.")
    })
    @PutMapping("/me/profile")
    public ApiResponse<Boolean> completeProfile(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody ProfileRequestDto req
    ) {
        String providerId = principal.getUsername(); // providerId (CustomUserDetails.getUsername()가 providerId를 반환하도록 한 현재 설계)
        boolean updated = userService.completeProfile(providerId, req);
        return ApiResponse.onSuccess(SuccessCode.USER_PROFILE_UPDATE_SUCCESS, updated);
    }

    @Operation(summary = "유저 전체 정보", description = "유저의 모든 정보(auth 제외)를 GET합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER_2007", description = "전체 정보 조회 완료")
    })
    @GetMapping("/info")
    public ApiResponse<UserResponseDto> getAllUserInfo(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        String providerId = principal.getUsername();
        UserResponseDto allInfo = userService.getAllInfo(providerId);
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, allInfo);
    }

    @Operation(summary = "유저 메인페이지 정보", description = "메인페이지에서 표시할 유저의 정보를 GET합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER_2008", description = "메인페이지 정보 조회 완료")
    })
    @GetMapping("/info/mainpage")
    public ApiResponse<UserResponseDto> getMainPageInfo(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        String providerId = principal.getUsername();
        UserResponseDto basicInfo = userService.getBasicInfo(providerId);
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, basicInfo);
    }

    @Operation(summary = "유저 마이페이지 정보", description = "마이페이지에서 표시할 유저의 정보를 GET합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER_2009", description = "마이페이지 정보 조회 완료")
    })
    @GetMapping("/info/mypage")
    public ApiResponse<UserResponseDto> getMyPageInfo(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        String providerId = principal.getUsername();
        UserResponseDto myPageInfo = userService.getMypageInfo(providerId);
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, myPageInfo);
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
