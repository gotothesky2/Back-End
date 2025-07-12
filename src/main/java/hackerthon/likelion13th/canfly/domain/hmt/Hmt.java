package hackerthon.likelion13th.canfly.domain.hmt;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Table(name = "hmt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hmt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itId")
    private Long id;

    @Column(name = "pdfLink")
    private String pdfLink;

    @Column(name = "hmtGradeNum")
    private Byte gradeNum;

    @Column(name = "hmtTermNum")
    private Byte termNum;

    @Column(name = "uploadTime")
    private Date uploadTime;

    @Column(name = "rScore")
    private Float rScore;

    @Column(name = "iScore")
    private Float iScore;

    @Column(name = "aScore")
    private Float aScore;

    @Column(name = "sScore")
    private Float sScore;

    @Column(name = "eScore")
    private Float eScore;

    @Column(name = "cScore")
    private Float cScore;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
