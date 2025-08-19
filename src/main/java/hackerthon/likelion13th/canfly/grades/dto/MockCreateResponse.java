package hackerthon.likelion13th.canfly.grades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "모의고사 생성 응답")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MockCreateResponse {

    @Schema(description = "생성된 Mock ID", example = "101")
    private Long mockId;

    @Schema(description = "응시 연도", example = "2025")
    private Integer examYear;

    @Schema(description = "응시 월", example = "9")
    private Integer examMonth;

    @Schema(description = "학년", example = "3")
    private Integer examGrade;

    @Schema(description = "과목별 계산 결과")
    private List<SubjectCalculated> results;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "과목별 계산 결과")
    public static class SubjectCalculated {

        @Schema(description = "엑셀 B열 라벨", example = "국어")
        private String label;

        @Schema(description = "입력값(표준점수 또는 등급)", example = "129")
        private Integer input;

        @Schema(description = "백분위", example = "99")
        private Integer percentile;

        @Schema(description = "등급", example = "1")
        private Integer grade;

        @Schema(description = "누적 백분위(%)", example = "1.25")
        private BigDecimal cumulative;

        @Schema(description = "카테고리 코드 (1:국어, 2:수학, 3:영어, 4:한국사, 5:탐구1, 6:탐구2, 7:제2외국어)", example = "1")
        private Integer category;

        @Schema(description = "세부 과목명(엔티티 name에 저장)", example = "국어 / 물리학 Ⅰ / 사회·문화 등")
        private String name;
    }
}