package hackerthon.likelion13th.canfly.domain.major;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.field.Field;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "major")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Major extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mId")
    private Long id;

    @Column(name = "mName", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fId")
    private Field field;
}
