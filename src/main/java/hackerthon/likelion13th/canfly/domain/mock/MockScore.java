package hackerthon.likelion13th.canfly.domain.mock;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "mockScore")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockScore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msId")
    private Long id;

    /** 표준점수 */
    @Column(name = "standardScore", nullable = false)
    private Short standardScore;

    /** 백분위 */
    @Column(name = "percentile")
    private Short percentile;

    /** 등급 */
    @Column(name = "grade")
    private Short grade;

    /** 누적 백분위(%) */
    @Column(name = "cumulative", precision = 5, scale = 2)
    private BigDecimal cumulative;

    /** 과목 카테고리 (예: 국어/수학) */
    @Column(name = "category", length = 50)
    private String category;

    /** 영역(세부 과목명) */
    @Column(name = "name", length = 50)
    private String name;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "mockId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Mock mock;

}
