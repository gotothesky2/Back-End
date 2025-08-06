package hackerthon.likelion13th.canfly.login.repository;

import hackerthon.likelion13th.canfly.domain.user.OAuth;
import hackerthon.likelion13th.canfly.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    Optional<OAuth> findByUser(User user);

    @Query("""
           select o
           from OAuth o
           join fetch o.user
           where o.providerUserId = :providerUserId
           """)
    Optional<OAuth> findByProviderUserId(@Param("providerUserId") String providerUserId);
}
