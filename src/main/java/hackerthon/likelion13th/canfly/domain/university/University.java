package hackerthon.likelion13th.canfly.domain.university;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.major.Major;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "university",
        uniqueConstraints = @UniqueConstraint(name = "uk_univ_name", columnNames = "univName"))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class University extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univId")
    private Long id;

    @Column(name = "univName", length = 100, nullable = false, unique = true)
    private String name;

    /* ────────── 학과 매핑 (다대다) ────────── */
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name               = "university_major",
            joinColumns        = @JoinColumn(name = "univId"),
            inverseJoinColumns = @JoinColumn(name = "mId"),
            uniqueConstraints  = @UniqueConstraint(columnNames = {"univId", "mId"})
    )
    private Set<Major> majors = new HashSet<>();
}