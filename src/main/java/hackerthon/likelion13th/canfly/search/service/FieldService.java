package hackerthon.likelion13th.canfly.search.service;

import hackerthon.likelion13th.canfly.domain.field.Field;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.search.dto.FieldDto;
import hackerthon.likelion13th.canfly.search.dto.MajorDto;
import hackerthon.likelion13th.canfly.search.repository.FieldBookmarkRepository;
import hackerthon.likelion13th.canfly.search.repository.FieldRepository;
import hackerthon.likelion13th.canfly.search.repository.MajorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FieldService {
    private final FieldBookmarkRepository fieldBookmarkRepository;
    private final FieldRepository fieldRepository;
    private final MajorRepository majorRepository;

    @Transactional
    public Field toggleFieldLike(Long fieldId, User user) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new GeneralException(ErrorCode.FIELD_NOT_FOUND));

        boolean isMarked = fieldBookmarkRepository.existsByUserAndField(user, field);

        if (isMarked) {
            fieldBookmarkRepository.deleteByUserAndField(user, field);
        } else {
            fieldBookmarkRepository.insertOrUpdateFieldMark(user.getUid(), field.getId()); // 좋아요 추가 (중복 삽입 방지)
        }

        return field;
    }

    public List<FieldDto> getAllLikedFields(User user) {
        List<Object[]> raw = fieldBookmarkRepository.findAllLikedFields(user.getUid());

        return raw.stream()
                .map(row -> new FieldDto(
                        ((Number) row[0]).longValue(), // f_id
                        (String) row[1]                // f_name
                ))
                .toList();
    }

    public List<FieldDto> getAllFields() {
        List<Field> fields = fieldRepository.findAll();

        return fields.stream()
                .map(f -> new FieldDto(f.getId(), f.getName()))
                .toList();
    }

    public List<MajorDto> getMajorsByField(Long fieldId) {
        List<Object[]> rows = majorRepository.findMajorsByFieldId(fieldId);
        return rows.stream()
                .map(r -> new MajorDto(
                        ((Number) r[0]).longValue(), // m_id
                        (String) r[1]                // m_name
                ))
                .toList();
    }
}
