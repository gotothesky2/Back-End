package hackerthon.likelion13th.canfly.domain.user;

import hackerthon.likelion13th.canfly.domain.cst.Cst;
import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.entity.Sex;
import hackerthon.likelion13th.canfly.domain.field.FieldBookmark;
import hackerthon.likelion13th.canfly.domain.hmt.Hmt;
import hackerthon.likelion13th.canfly.domain.major.MajorBookmark;
import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.report.Report;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "uid", updatable = false, nullable = false, length = 36)
    private String uid;

    @Column(name = "name", length = 40)
    private String name;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "phoneNumber", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private Sex sex;

    @Column(name = "highschool", length = 40)
    private String highschool;

    @Column(name = "gradeNum")
    private Byte gradeNum;

    @Column(nullable = false)
    private int token = 30;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private OAuth auth;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hmt> hmtResult = new ArrayList<>();
    public void addHmt(Hmt hmt) {
        this.hmtResult.add(hmt);
        hmt.setUser(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cst> cstResult = new ArrayList<>();
    public void addCst(Cst cst) {
        this.cstResult.add(cst);
        cst.setUser(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldBookmark> fieldBookmarkList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MajorBookmark> majorBookmarkList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mock> mockList = new ArrayList<>();
    public void addMock(Mock mock) {
        this.mockList.add(mock);
        mock.setUser(this);
    }


    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportList = new ArrayList<>();
    public void addReport(Report report) {
        this.reportList.add(report);
        report.setUser(this);
    }


}
