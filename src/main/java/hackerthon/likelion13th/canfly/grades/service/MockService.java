package hackerthon.likelion13th.canfly.grades.service;


import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.grades.dto.MockCreateRequest;
import hackerthon.likelion13th.canfly.grades.dto.MockCreateResponse;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.repository.MockRepository;
import hackerthon.likelion13th.canfly.grades.repository.MockScoreRepository;
import hackerthon.likelion13th.canfly.login.repository.UserRepository;
import hackerthon.likelion13th.canfly.score.ScoreExcelEngine;
import hackerthon.likelion13th.canfly.score.SubjectCategoryMapper;
import hackerthon.likelion13th.canfly.score.SubjectRegistry;
import hackerthon.likelion13th.canfly.score.SubjectSelectionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class MockService {
    private final MockRepository mockRepository;
    private final MockScoreRepository mockScoreRepository;
    private final UserRepository userRepository;

    private final SubjectSelectionValidator selectionValidator;
    private final ScoreExcelEngine excelEngine;

    @Transactional
    public MockResponseDto createMock(String userId, MockRequestDto mockRequestDto) {
        User user = userRepository.findByUid(userId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.USER_NOT_FOUND));

        Mock newMock = Mock.builder()
                .examYear(mockRequestDto.getExamYear())
                .examMonth(mockRequestDto.getExamMonth())
                .examGrade(mockRequestDto.getExamGrade())
                .user(user)
                .build();

        // MockScoreRequestDto 리스트를 MockScore 엔티티 리스트로 변환하여 추가
        if (mockRequestDto.getScoreLists() != null && !mockRequestDto.getScoreLists().isEmpty()) {
            for (MockRequestDto.MockScoreRequestDto scoreDto : mockRequestDto.getScoreLists()) {
                MockScore mockScore = MockScore.builder()
                        .standardScore(scoreDto.getStandardScore())
                        .percentile(scoreDto.getPercentile())
                        .grade(scoreDto.getGrade())
                        .cumulative(scoreDto.getCumulative())
                        .category(scoreDto.getCategory())
                        .name(scoreDto.getName())
                        .build();
                newMock.addMockScore(mockScore);
            }
        }
        user.addMock(newMock);
        mockRepository.save(newMock);

        return new MockResponseDto(newMock);
    }

    @Transactional
    public MockResponseDto addMockScoreToMock(Long mockId, MockRequestDto.MockScoreRequestDto scoreRequestDto) {
        Mock mock = mockRepository.findById(mockId)
                .orElseThrow(() -> new IllegalArgumentException("Mock not found with id: " + mockId));

        // DTO 필드에서 MockScore 엔티티 직접 생성
        MockScore newMockScore = MockScore.builder()
                .standardScore(scoreRequestDto.getStandardScore())
                .percentile(scoreRequestDto.getPercentile())
                .grade(scoreRequestDto.getGrade())
                .cumulative(scoreRequestDto.getCumulative())
                .category(scoreRequestDto.getCategory())
                .name(scoreRequestDto.getName())
                .build();
        mock.addMockScore(newMockScore); // Mock 엔티티에 MockScore 추가 (양방향 관계 설정)
        mockScoreRepository.save(newMockScore);
        return new MockResponseDto(mock);
    }

    @Transactional(readOnly = true)
    public MockResponseDto getMockById(Long mockId) {
        Mock mock = mockRepository.findById(mockId)
                .orElseThrow(() -> new IllegalArgumentException("Mock not found with ID: " + mockId));
        return new MockResponseDto(mock);
    }

    @Transactional
    public List<MockResponseDto> getAllMocksByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        List<Mock> mocks = mockRepository.findByUser(userRepository.findByUid(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId)));

        return mocks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MockResponseDto.MockScoreResponseDto getMockScoreById(Long mockId, Long scoreId) {
        Mock mock = mockRepository.findById(mockId)
                .orElseThrow(() -> new IllegalArgumentException("Mock not found with id: " + mockId));

        MockScore mockScore = mock.getScoreLists().stream()
                .filter(score -> score.getId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("MockScore not found with id: " + scoreId + " in Mock: " + mockId));

        return new MockResponseDto.MockScoreResponseDto(mockScore);
    }

    @Transactional
    public MockResponseDto updateMock(Long mockId, MockRequestDto mockRequestDto) {
        Mock existingMock = mockRepository.findById(mockId)
                .orElseThrow(() -> new IllegalArgumentException("Mock not found with id: " + mockId));
        existingMock.setExamYear(mockRequestDto.getExamYear());
        existingMock.setExamMonth(mockRequestDto.getExamMonth());
        existingMock.setExamGrade(mockRequestDto.getExamGrade());
        Mock updatedMock = mockRepository.save(existingMock);
        return convertToDto(updatedMock);
    }

    @Transactional
    public MockResponseDto.MockScoreResponseDto updateMockScore(Long mockScoreId, MockRequestDto.MockScoreRequestDto scoreRequestDto) {
        MockScore existingMockScore = mockScoreRepository.findById(mockScoreId)
                .orElseThrow(() -> new IllegalArgumentException("MockScore not found with id: " + mockScoreId));
        existingMockScore.setStandardScore(scoreRequestDto.getStandardScore());
        existingMockScore.setPercentile(scoreRequestDto.getPercentile());
        existingMockScore.setGrade(scoreRequestDto.getGrade());
        existingMockScore.setCumulative(scoreRequestDto.getCumulative());
        existingMockScore.setCategory(scoreRequestDto.getCategory());
        existingMockScore.setName(scoreRequestDto.getName());
        MockScore updatedMockScore = mockScoreRepository.save(existingMockScore);
        return convertToDto(updatedMockScore);
    }

    @Transactional
    public void deleteMock(Long mockId) {
        mockRepository.deleteById(mockId);
    }

    private MockResponseDto convertToDto(Mock mock) {
        // MockResponseDto의 생성자를 호출하여 엔티티를 넘겨줍니다.
        return new MockResponseDto(mock);
        }
    private MockResponseDto.MockScoreResponseDto convertToDto(MockScore mockScore) {
        // MockResponseDto의 생성자를 호출하여 엔티티를 넘겨줍니다.
        return new MockResponseDto.MockScoreResponseDto(mockScore);
    }

    // 여기서부터 지피티 코드 -> 엑셀 추출 + 업서트 반영
    /**
     * @param uid  현재 로그인 사용자 UID (또는 providerId 등, 레포지토리에 맞게 변경)
     * @param req  모의고사 생성/갱신 요청
     * @return 생성 또는 갱신 결과(계산 결과 포함)
     */
    @Transactional
    public MockCreateResponse createMockByExcel(String uid, MockCreateRequest req) {
        // 0) 사용자 조회
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. uid=" + uid));

        // 1) 원본 입력을 엔진 형태로 변환 + 선택/조합/범위 검증
        var rawInputs = req.getInputs().stream()
                .map(i -> new ScoreExcelEngine.SubjectInput(i.getLabel(), i.getValue()))
                .collect(Collectors.toList());

        var validated = selectionValidator.validateToEngineInputs(rawInputs);
        var engineInputs = validated.engineInputs();

        // 2) 엑셀 평가 (메모리에서만)
        var results = excelEngine.evaluateAll(engineInputs);

        // 3) 과목 순서(탐구1/탐구2 코드 부여를 위해) - 라벨 → Subject
        List<SubjectRegistry.Subject> pickedOrder = engineInputs.stream()
                .map(in -> SubjectRegistry.byLabel(in.label()))
                .toList();

        // 4) 0/0/0 가드: 표준점수 과목에서 백분위=0 & 등급=0 & 누적=0 이면 입력 오류
        for (var in : engineInputs) {
            SubjectRegistry.Subject subj = SubjectRegistry.byLabel(in.label());
            if (subj.getInputType() == SubjectRegistry.InputType.GRADE) continue; // 등급 입력 과목은 스킵

            var r = results.get(in.label());
            if (r == null) throw new IllegalStateException("엑셀 평가 결과 없음: " + in.label());

            int pct  = r.percentile() == null ? 0 : r.percentile();
            int grd  = r.grade() == null ? 0 : r.grade();
            boolean cumZero = (r.cumulative() == null) || r.cumulative().compareTo(BigDecimal.ZERO) == 0;

            if (pct == 0 && grd == 0 && cumZero) {
                throw new IllegalArgumentException("입력 오류로 판단됩니다. 과목='" + in.label() + "', 입력값=" + in.value()
                        + " (계산 결과: 백분위=0, 등급=0, 누적=0)");
            }
        }

        // 5) 기존 Mock 조회 (있으면 재사용)
        Mock mock = mockRepository.findByUserUidAndExamYearAndExamMonthAndExamGrade(
                user.getUid(), req.getExamYear(), req.getExamMonth(), req.getExamGrade()
        ).orElseGet(() ->
                Mock.builder()
                        .examYear(req.getExamYear())
                        .examMonth(req.getExamMonth())
                        .examGrade(req.getExamGrade())
                        .user(user)
                        .build()
        );

        // 6) 자식 전량 교체: 기존 점수들 제거 후, 이번 계산 결과로 다시 채움
        // orphanRemoval = true 이므로 리스트에서 제거하면 DB에서도 삭제됨
        mock.getScoreLists().clear();

        List<MockCreateResponse.SubjectCalculated> responseItems = new ArrayList<>();
        for (var in : engineInputs) {
            String label = in.label();
            Integer inputVal = toInt(in.value());
            var subj = SubjectRegistry.byLabel(label);
            var r = results.get(label);

            Integer idxInSciSoc = SubjectCategoryMapper.sciSocIndex(subj, pickedOrder);
            int categoryCode = SubjectCategoryMapper.toCategoryCode(subj, idxInSciSoc);

            Integer grade = (r.grade() != null) ? r.grade()
                    : (subj.getInputType() == SubjectRegistry.InputType.GRADE ? inputVal : null);
            if (grade == null) throw new IllegalArgumentException("등급 계산값을 결정할 수 없습니다. 과목='" + label + "'");

            MockScore ms = MockScore.builder()
                    .standardScore(subj.getInputType() == SubjectRegistry.InputType.SCORE ? inputVal : null)
                    .percentile(r.percentile())
                    .grade(grade)
                    .cumulative(scale2(r.cumulative()))
                    .category(categoryCode)
                    .name(label)
                    .build();

            mock.addMockScore(ms);

            responseItems.add(
                    MockCreateResponse.SubjectCalculated.builder()
                            .label(label).input(inputVal)
                            .percentile(r.percentile()).grade(grade).cumulative(scale2(r.cumulative()))
                            .category(categoryCode).name(label)
                            .build()
            );
        }

        // 7) 저장 (신규면 insert, 기존이면 update)
        Mock saved = mockRepository.save(mock);

        // 8) 응답 DTO 구성
        return MockCreateResponse.builder()
                .mockId(saved.getId())
                .examYear(saved.getExamYear())
                .examMonth(saved.getExamMonth())
                .examGrade(saved.getExamGrade())
                .results(responseItems)
                .build();
    }

    private Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        return Integer.valueOf(v.toString());
    }

    private BigDecimal scale2(BigDecimal v) {
        return (v == null) ? null : v.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}