package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MockScoreRepository extends JpaRepository<MockScore, Long> {
    Optional<MockScore> findById(Long id);
}
