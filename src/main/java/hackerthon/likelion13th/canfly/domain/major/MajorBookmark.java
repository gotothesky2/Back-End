package hackerthon.likelion13th.canfly.domain.major;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.university.University;
import hackerthon.likelion13th.canfly.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "major_bookmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorBookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "univ_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private University university;
}
