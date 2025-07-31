package hackerthon.likelion13th.canfly.search.repository;

import hackerthon.likelion13th.canfly.domain.major.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByCode(Integer code); // m_code == majorSeq
}