package hackerthon.likelion13th.canfly.domain.mock;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mockId")
    private Long id;

    @Column(name = "examYear", nullable = false)
    private Short examYear;

    @Column(name = "examMonth", nullable = false)
    private Byte examMonth;

    @Column(name = "examGrade", nullable = false)
    private Byte examGrade;

    /** 과목별 점수 리스트 */
    @OneToMany(mappedBy = "mock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MockScore> scoreList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
