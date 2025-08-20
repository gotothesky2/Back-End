package hackerthon.likelion13th.canfly.search.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnivMajorTupleDto {
    private Long majorId;
    private Long univId;
    private List<String> name;
}