package hackerthon.likelion13th.canfly.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDto {
    private Long id;      // field_id
    private String name;  // field_name
}