package hackerthon.likelion13th.canfly.grades.service;


import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.repository.MockRepository;
import hackerthon.likelion13th.canfly.grades.repository.MockScoreRepository;
import hackerthon.likelion13th.canfly.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class MockService {
    private final MockRepository mockRepository;
    private final MockScoreRepository mockScoreRepository;
    private final UserRepository userRepository;

    @Transactional
    public MockResponseDto createMock(String userId, MockRequestDto mockRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Mock newMock = Mock.builder()
                .examYear(mockRequestDto.getExamYear())
                .examMonth(mockRequestDto.getExamMonth())
                .examGrade(mockRequestDto.getExamGrade())
                .build();


        user.addMock(newMock); // User와 Mock 간의 양방향 관계 설정

        // MockScoreRequestDto 리스트를 MockScore 엔티티 리스트로 변환하여 추가
        if (mockRequestDto.getScoreLists() != null && !mockRequestDto.getScoreLists().isEmpty()) {
            for (MockRequestDto.MockScoreRequestDto scoreRequestDto : mockRequestDto.getScoreLists()) {
                MockScore mockScore = MockScore.builder() // MockScore 엔티티의 @Builder 사용
                        .standardScore(scoreRequestDto.getStandardScore())
                        .percentile(scoreRequestDto.getPercentile())
                        .grade(scoreRequestDto.getGrade())
                        .cumulative(scoreRequestDto.getCumulative())
                        .category(scoreRequestDto.getCategory())
                        .name(scoreRequestDto.getName())
                        .build();
                newMock.addMockScore(mockScore); // Mock과 MockScore 간의 양방향 관계 설정
            }
        }

        userRepository.save(user);
        mockRepository.save(newMock);

        return new MockResponseDto(newMock);
    }

    @Transactional(readOnly = true)
    public MockResponseDto getMockById(Long mockId) {
        Mock mock = mockRepository.findById(mockId)
                .orElseThrow(() -> new IllegalArgumentException("Mock not found with ID: " + mockId));
        return new MockResponseDto(mock);
    }
}