package hackerthon.likelion13th.canfly.grades.dto;

import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockResponseDto {
    private Long id;
    private Short examYear;
    private Byte examMonth;
    private Byte examGrade;
    private List<MockScoreResponseDto> scoreList = new ArrayList<>();

    public MockResponseDto(Mock mock) {
        this.id = mock.getId();
        this.examYear = mock.getExamYear();
        this.examMonth = mock.getExamMonth();
        this.examGrade = mock.getExamGrade();
        if (mock.getScoreLists() != null) {
            for (MockScore mockScore : mock.getScoreLists()) {
                // 각 MockScore 엔티티를 MockScoreResponseDto로 변환하여 리스트에 추가
                this.scoreList.add(new MockScoreResponseDto(mockScore));
            }
        }
        else {
            this.scoreList = new ArrayList<>();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockScoreResponseDto {
        private Long scoreId;
        private Short standardScore;
        private Short percentile;
        private Byte grade;
        private BigDecimal cumulative;
        private String category;
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