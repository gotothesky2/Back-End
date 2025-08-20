package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MockRepository extends JpaRepository<Mock, Long> {

    List<Mock> findByUser(User user);

    // 사용자(uid) + 연/월/학년으로 기존 모의고사 존재 여부 확인
    Optional<Mock> findByUserUidAndExamYearAndExamMonthAndExamGrade(
            String uid, Integer examYear, Integer examMonth, Integer examGrade
    );

    Optional<Mock> findByIdAndUser_Uid(Long mockId, String userId);

    void deleteByIdAndUser_Uid(Long id, String userUid);
}
