package hackerthon.likelion13th.canfly.grades.dto;

import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockResponseDto {
    private Long id;
    private Integer examYear;
    private Integer  examMonth;
    private Integer examGrade;
    private List<MockScoreResponseDto> scoreList = new ArrayList<>();

    public MockResponseDto(Mock mock) {
        this.id = mock.getId();
        this.examYear = mock.getExamYear();
        this.examMonth = mock.getExamMonth();
        this.examGrade = mock.getExamGrade();
        this.scoreList = (mock.getScoreLists() != null) && (!mock.getScoreLists().isEmpty()) ?
                mock.getScoreLists().stream()
                        .map(MockScoreResponseDto::new) // MockScoreResponseDto의 생성자 호출
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockScoreResponseDto {
        private Long scoreId;
        private Integer standardScore;
        private Integer percentile;
        private Integer grade;
        private BigDecimal cumulative;
        private Integer category;
        private String name;

        public MockScoreResponseDto(MockScore mockScore) {
            this.scoreId = mockScore.getId();
            this.standardScore = mockScore.getStandardScore();
            this.percentile = mockScore.getPercentile();
            this.grade = mockScore.getGrade();
            this.cumulative = mockScore.getCumulative();
            this.category = mockScore.getCategory();
            this.name = mockScore.getName();
        }
    }
}