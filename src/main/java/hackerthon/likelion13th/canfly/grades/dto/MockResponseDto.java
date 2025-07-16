package hackerthon.likelion13th.canfly.grades.dto;

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
    private List<MockScore> scoreList = new ArrayList<>();

    public static class MockScoreResponseDto{
        private Long scoreId;
        private Short standardScore;
        private Short percentile;
        private Short grade;
        private BigDecimal cumulative;
        private String category;
        private String name;
    }
}
