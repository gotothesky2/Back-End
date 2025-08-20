package hackerthon.likelion13th.canfly.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorDto {
    private Long id;      // major_id
    private String name;  // major_name
}
