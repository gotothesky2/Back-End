package hackerthon.likelion13th.canfly.domain.major;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.field.Field;
import hackerthon.likelion13th.canfly.domain.university.University;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "major")
@Getter
@Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Major extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mId")
    private Long id;

    @Column(name = "mName", length = 50, nullable = false)
    private String name;

    @Column(name = "mCode", nullable = false)
    private Integer code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fId")
    private Field field;

    @Builder.Default
    @ManyToMany(mappedBy = "majors")
    private Set<University> universities = new HashSet<>();
}
