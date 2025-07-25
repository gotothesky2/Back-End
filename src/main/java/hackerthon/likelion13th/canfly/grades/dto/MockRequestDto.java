package hackerthon.likelion13th.canfly.grades.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockRequestDto {

    private Short examYear;
    private Byte examMonth;
    private Byte examGrade;
    private List<MockScoreRequestDto> scoreLists;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockScoreRequestDto {
        private Short standardScore;
        private Short percentile;
        private Byte grade;
        private BigDecimal cumulative;
        private String category;
        private String name;
    }
}
