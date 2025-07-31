package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.domain.report.Report;
import hackerthon.likelion13th.canfly.domain.report.ReportScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportScoreRepository extends JpaRepository<ReportScore, Long> {
    Optional<ReportScore> findById(Long id);
}
