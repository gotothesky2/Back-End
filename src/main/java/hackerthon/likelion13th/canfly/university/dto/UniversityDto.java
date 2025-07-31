package hackerthon.likelion13th.canfly.university.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(description = "UnivDto")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UniversityDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "서울대학교")
    private String name;

    @Schema(description = "개설 학과 이름 목록")
    private Set<String> majors;
}