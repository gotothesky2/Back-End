package hackerthon.likelion13th.canfly.domain.user;

import hackerthon.likelion13th.canfly.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authId")
    private Long id;

    @Column(nullable = false)
    private String providerUserId;

    @Column(name = "accessToken", length = 255)
    private String accessToken;

    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    @Column(name = "expireDate")
    private LocalDateTime expireDate;

    @Column(name = "refreshToken", length = 255, nullable = false)
    private String refreshToken;

    // FK: uid → User(uid)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
