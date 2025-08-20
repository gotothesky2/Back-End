package hackerthon.likelion13th.canfly.search.repository;

import hackerthon.likelion13th.canfly.domain.field.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Long> {
}
