package hackerthon.likelion13th.canfly.login.dto;

import hackerthon.likelion13th.canfly.domain.entity.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Schema(description = "ProfileReqDto")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequestDto {

    @Schema(description = "성별")
    private Sex sex;

    @Schema(description = "고등학교")
    private String highschool;

    @Schema(description = "학년")
    private Byte gradeNum;

    @Schema(description = "우편번호")
    private String zipcode;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세주소")
    private String addressDetail;
}
