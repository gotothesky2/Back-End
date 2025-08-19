package hackerthon.likelion13th.canfly.domain.report;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "reportScore")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportScore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rsId")
    private Long id;

    @Column(name = "subject", length = 50, nullable = false)
    private String subject;          // 과목명

    @Column(name = "grade")
    private Integer grade;           // 등급

    @Column(name = "student_num")
    private Integer studentNum;      // 수강자 수

    @Column(name = "standard_deviation", precision = 4, scale = 1)
    private BigDecimal standardDeviation;// 표준편차

    @Column(name = "subject_average")
    private Integer subjectAverage;   // 평균

    @Column(name = "achievement")
    private String achievement;// 성취도(A,B,C)

    @Column(name = "score")
    private Integer score;            // 원점수

    @Column(name = "credit", nullable = false)
    private Integer credit;          // 학점      // 선택구분(일선/진선)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Report report;
}
