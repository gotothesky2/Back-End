package hackerthon.likelion13th.canfly.domain.cst;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Table(name = "cst")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cst extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atId")
    private Long id;

    @Column(name = "pdfLink")
    private String pdfLink;

    @Column(name = "cstGradeNum")
    private Byte gradeNum;

    @Column(name = "cstTermNum")
    private Byte termNum;

    @Column(name = "uploadTime")
    private Date uploadTime;

    @Column(name = "mathScore")
    private Float mathScore;

    @Column(name = "spaceScore")
    private Float spaceScore;

    @Column(name = "creativeScore")
    private Float creativeScore;

    @Column(name = "natureScore")
    private Float natureScore;

    @Column(name = "artScore")
    private Float artScore;

    @Column(name = "musicScore")
    private Float musicScore;

    @Column(name = "langScore")
    private Float langScore;

    @Column(name = "selfScore")
    private Float selfScore;

    @Column(name = "handScore")
    private Float handScore;

    @Column(name = "relationScore")
    private Float relationScore;

    @Column(name = "physicalScore")
    private Float physicalScore;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

}
