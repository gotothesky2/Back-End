package hackerthon.likelion13th.canfly.domain.major;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import hackerthon.likelion13th.canfly.domain.field.Field;
import hackerthon.likelion13th.canfly.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "majorBookmark")
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

    @Column(name = "majorRecommend")
    private Boolean isRecommend = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
