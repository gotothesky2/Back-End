package hackerthon.likelion13th.canfly.search.repository;

import hackerthon.likelion13th.canfly.domain.major.Major;
import hackerthon.likelion13th.canfly.domain.major.MajorBookmark;
import hackerthon.likelion13th.canfly.domain.university.University;
import hackerthon.likelion13th.canfly.domain.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MajorBookmarkRepository extends JpaRepository<MajorBookmark, Long> {

    boolean existsByUserAndMajor(User user, Major major);

    void deleteByUserAndMajor(User user, Major major);

    boolean existsByUserAndMajorAndUniversity(User user, Major major, University university);

    void deleteByUserAndMajorAndUniversity(User user, Major major, University university);

    @Modifying
    @Query(value = """
        INSERT INTO major_bookmark (uid, m_id, univ_id)
        VALUES (:userId, :majorId, NULL)
        ON DUPLICATE KEY UPDATE mb_id = LAST_INSERT_ID(mb_id)
        """, nativeQuery = true)
    void upsertMajorOnly(@org.springframework.data.repository.query.Param("userId") String userId,
                         @org.springframework.data.repository.query.Param("majorId") Long majorId);

    @Modifying
    @Query(value = """
        INSERT INTO major_bookmark (uid, m_id, univ_id)
        VALUES (:userId, :majorId, :univId)
        ON DUPLICATE KEY UPDATE mb_id = LAST_INSERT_ID(mb_id)
        """, nativeQuery = true)
    void upsertMajorWithUniv(@org.springframework.data.repository.query.Param("userId") String userId,
                             @org.springframework.data.repository.query.Param("majorId") Long majorId,
                             @org.springframework.data.repository.query.Param("univId") Long univId);

    @Query(value = """
    SELECT m.m_id AS id, m.m_name AS name
    FROM major_bookmark mb
    JOIN major m ON mb.m_id = m.m_id
    WHERE mb.uid = :userId
    GROUP BY m.m_id, m.m_name
    ORDER BY COALESCE(MAX(mb.created_at), '1000-01-01 00:00:00') DESC,
             MAX(mb.mb_id) DESC
    """, nativeQuery = true)
    List<Object[]> findAllLikedMajors(@Param("userId") String userId);
}
