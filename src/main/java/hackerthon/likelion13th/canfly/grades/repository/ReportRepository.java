package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.report.Report;
import hackerthon.likelion13th.canfly.domain.report.ReportScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportScore, Long> {

}
