package hackerthon.likelion13th.canfly.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "for coin(token) DTO")
@Getter
@Setter
public class CoinRequestDto {
    private int amount;
}