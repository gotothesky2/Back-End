package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.report.Report;
import hackerthon.likelion13th.canfly.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {


    List<Report> findByUser(User user);

    Optional<Report> findByUserAndUserGradeAndTermAndCategoryName(
            User user, Integer userGrade, Integer term, Integer categoryName
    );

    Optional<Report> findByIdAndUser_Uid(Long reportId, String userId);

    void deleteByIdAndUser_Uid(Long reportId, String userId);
}
