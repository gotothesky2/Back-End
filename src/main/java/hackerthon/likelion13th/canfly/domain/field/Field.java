package hackerthon.likelion13th.canfly.domain.field;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.major.Major;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "field")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fId")
    private Long id;

    @Column(name = "fName", length = 50, nullable = false)
    private String name;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Major> majors = new ArrayList<>();
}
