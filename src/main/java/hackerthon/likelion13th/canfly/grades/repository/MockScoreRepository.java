package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.mock.MockScore;

import java.util.Optional;

public interface MockScoreRepository {
    Optional<MockScore> findById(int id);
}
