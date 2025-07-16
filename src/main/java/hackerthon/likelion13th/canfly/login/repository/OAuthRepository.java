package hackerthon.likelion13th.canfly.login.repository;

import hackerthon.likelion13th.canfly.domain.user.OAuth;
import hackerthon.likelion13th.canfly.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    Optional<Object> findByUser(User user);
}
