package hackerthon.likelion13th.canfly.grades.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private Integer userGrade;
    private Integer term;
    private Integer categoryName;
    private List<ReportScoreRequestDto> scoreLists;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportScoreRequestDto {
        private Long scoreId;
        private String subject;
        private Integer grade;
        private Integer studentNum;
        private BigDecimal standardDeviation;
        private Integer subjectAverage;
        private String achievement;
        private Integer score;
        private Integer credit;
    }
}
