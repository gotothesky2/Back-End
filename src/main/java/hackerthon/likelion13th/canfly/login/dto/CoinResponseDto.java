package hackerthon.likelion13th.canfly.login.dto;

import hackerthon.likelion13th.canfly.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoinResponseDto {
    private String username;
    private int currentCoin; // 처리 후 최종 토큰 개수

    // User 엔티티를 받아서 DTO로 변환해주는 정적 팩토리 메소드
    public static CoinResponseDto fromEntity(User user) {
        return new CoinResponseDto(user.getName(), user.getToken());
    }
}