package hackerthon.likelion13th.canfly.grades.repository;

import hackerthon.likelion13th.canfly.domain.mock.Mock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockRepository extends JpaRepository<Mock, Long> {
}
