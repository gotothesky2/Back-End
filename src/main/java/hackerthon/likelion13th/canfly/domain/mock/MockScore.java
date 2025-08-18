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
    @Column(name = "standardScore")
    private Integer standardScore;

    /** 백분위 */
    @Column(name = "percentile")
    private Integer percentile;

    /** 등급 */
    @Column(name = "grade", nullable = false)
    private Integer grade;

    /** 누적 백분위(%) */
    @Column(name = "cumulative", precision = 5, scale = 2)
    private BigDecimal cumulative;

    /** 과목 카테고리 (예: 국어/수학) -> 국어 1 , 수학 2, 영어 3, 한국사 4, 탐구1 5, 탐구2 6, 제2외국어 7*/
    @Column(name = "category", nullable = false)
    private Integer category;

    /** 영역(세부 과목명) */
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "mockId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Mock mock;

}
