package hackerthon.likelion13th.canfly.domain.university;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "university")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class University extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univId", nullable = false)
    private Integer id;

    @Column(name = "univName", length = 50)
    private String name;

    
}
