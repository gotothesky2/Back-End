package hackerthon.likelion13th.canfly.login.converter;

import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.login.auth.dto.JwtDto;
import hackerthon.likelion13th.canfly.login.dto.UserRequestDto;

public class UserConverter {
    public static User saveUser(UserRequestDto userReqDto) {
        return User.builder()
                .email(userReqDto.getEmail())
                .name(userReqDto.getUsername())
                .build();
    }

    public static JwtDto jwtDto(String access, String refresh, String signIn) {
        return JwtDto.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .signIn(signIn)
                .build();
    }
}
