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
    private Integer examYear;

    @Column(name = "examMonth", nullable = false)
    private Integer examMonth;

    @Column(name = "examGrade", nullable = false)
    private Integer examGrade;

    /** 과목별 점수 리스트 */
    @OneToMany(mappedBy = "mock", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MockScore> scoreLists = new ArrayList<>();
    public void addMockScore(MockScore mockScore) {
        this.scoreLists.add(mockScore);
        mockScore.setMock(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
