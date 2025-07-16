package hackerthon.likelion13th.canfly.grades.dto;

import hackerthon.likelion13th.canfly.domain.mock.Mock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockRequestDto {
    private Short examYear;
    private String examMonth;

    public static class MockScoreRequestDto {
        private String category;
        private Mock mock;
    }
}
