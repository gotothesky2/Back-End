package hackerthon.likelion13th.canfly.search.repository;

import hackerthon.likelion13th.canfly.domain.field.Field;
import hackerthon.likelion13th.canfly.domain.field.FieldBookmark;
import hackerthon.likelion13th.canfly.domain.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldBookmarkRepository extends JpaRepository<FieldBookmark, Long> {

    boolean existsByUserAndField(User user, Field field);

    void deleteByUserAndField(User user, Field field);

    @Modifying
    @Query(value = "INSERT INTO field_bookmark (uid, f_id) VALUES (:userId, :fieldId) " +
            "ON DUPLICATE KEY UPDATE fb_id = LAST_INSERT_ID(fb_id)", nativeQuery = true)
    void insertOrUpdateFieldMark(@Param("userId") String userId, @Param("fieldId") Long fieldId);

    @Query(value = """
        SELECT f.f_id AS id, f.f_name AS name
        FROM field_bookmark fb
        JOIN field f ON fb.f_id = f.f_id
        WHERE fb.uid = :userId
        ORDER BY fb.created_at DESC, fb.fb_id DESC
        """, nativeQuery = true)
    List<Object[]> findAllLikedFields(@Param("userId") String userId);
}
