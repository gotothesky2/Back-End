package hackerthon.likelion13th.canfly.grades.dto;

import hackerthon.likelion13th.canfly.domain.report.Report;
import hackerthon.likelion13th.canfly.domain.report.ReportScore;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private Long id;
    private Integer userGrade;
    private Integer term;
    private Integer categoryName;
    private BigDecimal categoryGrade;
    private List<ReportScoreResponseDto> scoreList = new ArrayList<>();

    public ReportResponseDto(Report report) {
        this.id = report.getId();
        this.userGrade = report.getUserGrade();
        this.term = report.getTerm();
        this.categoryName = report.getCategoryName();
        this.categoryGrade = report.getCategoryGrade();
        this.scoreList = report.getScoreLists().stream()
                        .map(ReportScoreResponseDto::new) // ReportScoreResponseDto의 생성자 호출
                        .collect(Collectors.toList());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportScoreResponseDto {
        private Long scoreId;
        private String subject;
        private Integer grade;
        private Integer studentNum;
        private BigDecimal standardDeviation;
        private Integer subjectAverage;
        private String achievement;
        private Integer score;
        private Integer credit;

        public ReportScoreResponseDto(ReportScore reportScore) {
            this.scoreId = reportScore.getId();
            this.subject = reportScore.getSubject();
            this.grade = reportScore.getGrade();
            this.studentNum = reportScore.getStudentNum();
            this.standardDeviation = reportScore.getStandardDeviation();
            this.subjectAverage = reportScore.getSubjectAverage();
            this.achievement = reportScore.getAchievement();
            this.score = reportScore.getScore();
            this.credit = reportScore.getCredit();
        }
    }
}