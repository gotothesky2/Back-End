package hackerthon.likelion13th.canfly.grades.service;


import hackerthon.likelion13th.canfly.domain.mock.Mock;
import hackerthon.likelion13th.canfly.domain.mock.MockScore;
import hackerthon.likelion13th.canfly.grades.dto.MockRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.MockResponseDto;
import hackerthon.likelion13th.canfly.grades.repository.MockRepository;
import hackerthon.likelion13th.canfly.grades.repository.MockScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MockService {
    private final MockRepository mockRepository;
    private final MockScoreRepository mockScoreRepository;

    public MockResponseDto getMockById(Long id) {
        Mock mock = mockRepository.findById(id).get();
        MockResponseDto mockResponseDto = new MockResponseDto();
        mockResponseDto.setId(mock.getId());
        mockResponseDto.setExamYear(mock.getExamYear());
        mockResponseDto.setExamMonth(mock.getExamMonth());
        mockResponseDto.setExamGrade(mock.getExamGrade());
        mockResponseDto.setScoreList(mock.getScoreList());
        return mockResponseDto;
    }

    public List<MockScore> getScoreList(Long scoreId) {
        List<MockScore> mockScoreList = new ArrayList<>();
        MockScore mockScore = mockScoreRepository.findById(scoreId).get();

    }
}
