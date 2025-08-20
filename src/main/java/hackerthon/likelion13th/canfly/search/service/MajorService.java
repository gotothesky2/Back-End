package hackerthon.likelion13th.canfly.search.service;

import hackerthon.likelion13th.canfly.domain.major.Major;
import hackerthon.likelion13th.canfly.domain.university.University;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.search.repository.MajorBookmarkRepository;
import hackerthon.likelion13th.canfly.search.repository.MajorRepository;
import hackerthon.likelion13th.canfly.university.repository.UniversityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MajorService {
    private final MajorBookmarkRepository majorBookmarkRepository;
    private final MajorRepository majorRepository;
    private final UniversityRepository universityRepository;

    @Transactional
    public Major toggleMajorLike(Long majorId, User user) {
        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> new GeneralException(ErrorCode.MAJOR_NOT_FOUND));

        boolean isMarked = majorBookmarkRepository.existsByUserAndMajor(user, major);

        if (isMarked) {
            majorBookmarkRepository.deleteByUserAndMajor(user, major);
        } else {
            majorBookmarkRepository.upsertMajorOnly(user.getUid(), major.getId()); // 좋아요 추가 (중복 삽입 방지)
        }

        return major;
    }

    @Transactional
    public void toggleMajorUnivLike(Long majorId, Long univId, User user) {
        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> new GeneralException(ErrorCode.MAJOR_NOT_FOUND));
        University univ = universityRepository.findById(univId)
                .orElseThrow(() -> new GeneralException(ErrorCode.UNIVERSITY_NOT_FOUND));

        boolean exists = majorBookmarkRepository.existsByUserAndMajorAndUniversity(user, major, univ);
        if (exists) {
            majorBookmarkRepository.deleteByUserAndMajorAndUniversity(user, major, univ);
        } else {
            majorBookmarkRepository.upsertMajorWithUniv(user.getUid(), major.getId(), univ.getId());
        }
    }
}
