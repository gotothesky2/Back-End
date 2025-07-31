package hackerthon.likelion13th.canfly.grades.service;


import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.repository.MockRepository;
import hackerthon.likelion13th.canfly.grades.repository.MockScoreRepository;
import hackerthon.likelion13th.canfly.login.auth.mapper.CustomUserDetails;
import hackerthon.likelion13th.canfly.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class MockService {
    private final MockRepository mockRepository;
    private final MockScoreRepository mockScoreRepository;
    private final UserRepository userRepository;

    @Transactional
    public Mock findById(Long mockId) {
        return mockRepository.findById(mockId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.MOCK_NOT_FOUND));
    }

    @Transactional
    public MockResponseDto createMock(String userId, MockRequestDto mockRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

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

        List<Mock> mocks = mockRepository.findByUser(userRepository.findByUid(userId));

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
}