package hackerthon.likelion13th.canfly.domain.report;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.domain.entity.CategoryName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rId")
    private Long id;

    @Column(name = "user_grade", nullable = false)
    private Integer userGrade;

    @Column(name = "term", nullable = false)
    private Integer term;

    /** 과목 카테고리명 (국어/수학) */
    @Column(name = "category_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryName categoryName;

    /** 카테고리 평균 성적(예: 3.75) */
    @Column(name = "category_grade", precision = 5, scale = 2)
    private BigDecimal categoryGrade;


    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReportScore> scoreLists = new ArrayList<>();
    public void addReportScore(ReportScore reportScore) {
        this.scoreLists.add(reportScore);
        reportScore.setReport(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
