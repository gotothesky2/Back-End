package hackerthon.likelion13th.canfly.search.repository;

import hackerthon.likelion13th.canfly.domain.major.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByCode(Integer code); // m_code == majorSeq

    @Query(value = """
        SELECT m.m_id AS id, m.m_name AS name
        FROM major m
        WHERE m.f_id = :fieldId
        ORDER BY m.m_name
        """, nativeQuery = true)
    List<Object[]> findMajorsByFieldId(@Param("fieldId") Long fieldId);
}