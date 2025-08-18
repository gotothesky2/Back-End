package hackerthon.likelion13th.canfly.grades.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Schema(description = "모의고사 생성 요청")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MockCreateRequest {

    @Schema(description = "응시 연도", example = "2025", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(2000) @Max(2100)
    private Integer examYear;

    @Schema(description = "응시 월(3,6,9,11)", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(1) @Max(12)
    private Integer examMonth;

    @Schema(description = "학년(고1~3)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(1) @Max(3)
    private Integer examGrade;

    @Schema(description = "과목 입력 목록 (라벨은 엑셀 B열 텍스트와 동일해야 함)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ScoreInput> inputs;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "단일 과목 입력 (label=엑셀 B열 텍스트, value=표준점수 또는 등급)")
    public static class ScoreInput {
        @Schema(example = "국어", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String label;

        @Schema(example = "129", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private Integer value; // 표준점수 or 등급(영어/한국사/제2외국어)
    }
}