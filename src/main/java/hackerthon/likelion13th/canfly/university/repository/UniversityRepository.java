package hackerthon.likelion13th.canfly.university.repository;

import hackerthon.likelion13th.canfly.domain.university.University;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByName(String name);

    @Query(value = """
        SELECT DISTINCT u.univ_id AS id, u.univ_name AS name
        FROM university_major um
        JOIN university u ON u.univ_id = um.univ_id
        WHERE um.m_id = :majorId
        ORDER BY u.univ_name
        """, nativeQuery = true)
    List<Object[]> findUniversitiesByMajorId(@Param("majorId") Long majorId);
}
