package hackerthon.likelion13th.canfly.university.repository;

import hackerthon.likelion13th.canfly.domain.university.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByName(String name);
}
