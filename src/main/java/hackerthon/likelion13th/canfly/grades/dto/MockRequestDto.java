package hackerthon.likelion13th.canfly.grades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockRequestDto {

    private Integer examYear;
    private Integer examMonth;
    private Integer examGrade;
    private List<MockScoreRequestDto> scoreLists;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockScoreRequestDto {
        private Integer standardScore;
        private Integer percentile;
        private Integer grade;
        private BigDecimal cumulative;
        private Integer category;
        private String name;
    }
}
