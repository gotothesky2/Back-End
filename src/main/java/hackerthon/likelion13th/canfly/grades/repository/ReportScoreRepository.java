package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.report.ReportScore;

import java.util.Optional;

public interface ReportScoreRepository {
    Optional<ReportScore> findById(Long id);
}
