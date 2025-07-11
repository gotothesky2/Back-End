package hackerthon.likelion13th.canfly.domain.report;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Column(name = "ranking")
    private Integer ranking;         // 석차

    @Column(name = "student_num")
    private Integer studentNum;      // 수강자 수

    @Column(name = "standard_deviation", length = 50)
    private String standardDeviation;// 표준편차

    @Column(name = "subject_average", length = 50)
    private String subjectAverage;   // 평균

    @Column(name = "achievement", length = 50)
    private String achievement;      // 성취도(A,B,C)

    @Column(name = "score", length = 50)
    private String score;            // 원점수

    @Column(name = "term", nullable = false)
    private Integer term;            // 학기 구분

    @Column(name = "credit", nullable = false)
    private Integer credit;          // 학점

    @Column(name = "choice", nullable = false)
    private Integer choice;          // 선택구분(일선/진선)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Report report;
}
