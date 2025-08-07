package hackerthon.likelion13th.canfly.grades.service;


import hackerthon.likelion13th.canfly.domain.report.Report;
import hackerthon.likelion13th.canfly.domain.report.ReportScore;
import hackerthon.likelion13th.canfly.domain.user.User;
import hackerthon.likelion13th.canfly.global.api.ErrorCode;
import hackerthon.likelion13th.canfly.global.exception.GeneralException;
import hackerthon.likelion13th.canfly.grades.dto.ReportRequestDto;
import hackerthon.likelion13th.canfly.grades.dto.ReportResponseDto;
import hackerthon.likelion13th.canfly.grades.repository.ReportRepository;
import hackerthon.likelion13th.canfly.grades.repository.ReportScoreRepository;
import hackerthon.likelion13th.canfly.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportScoreRepository reportScoreRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportResponseDto createReport(String userId, ReportRequestDto reportRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Report newReport = Report.builder()
                .categoryName(reportRequestDto.getCategoryName())
                .categoryGrade(BigDecimal.valueOf(0.0))
                .term(reportRequestDto.getTerm())
                .userGrade(reportRequestDto.getUserGrade())
                .user(user)
                .build();

        // ReportScoreRequestDto 리스트를 ReportScore 엔티티 리스트로 변환하여 추가
        if (reportRequestDto.getScoreLists() != null && !reportRequestDto.getScoreLists().isEmpty()) {
            for (ReportRequestDto.ReportScoreRequestDto scoreDto : reportRequestDto.getScoreLists()) {
                ReportScore reportScore = ReportScore.builder()
                        .subject(scoreDto.getSubject())
                        .grade(scoreDto.getGrade())
                        .achievement(scoreDto.getAchievement())
                        .ranking(scoreDto.getRanking())
                        .studentNum(scoreDto.getStudentNum())
                        .standardDeviation(scoreDto.getStandardDeviation())
                        .subjectAverage(scoreDto.getSubjectAverage())
                        .score(scoreDto.getScore())
                        .credit(scoreDto.getCredit())
                        .build();
                newReport.addReportScore(reportScore);
            }
        }
        user.addReport(newReport);
        return new ReportResponseDto(newReport);
    }

    @Transactional
    public ReportResponseDto addReportScoreToReport(Long reportId, ReportRequestDto.ReportScoreRequestDto scoreRequestDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));

        // DTO 필드에서 ReportScore 엔티티 직접 생성
        ReportScore newReportScore = ReportScore.builder()
                .subject(scoreRequestDto.getSubject())
                .grade(scoreRequestDto.getGrade())
                .achievement(scoreRequestDto.getAchievement())
                .ranking(scoreRequestDto.getRanking())
                .studentNum(scoreRequestDto.getStudentNum())
                .standardDeviation(scoreRequestDto.getStandardDeviation())
                .subjectAverage(scoreRequestDto.getSubjectAverage())
                .score(scoreRequestDto.getScore())
                .credit(scoreRequestDto.getCredit())
                .build();
        report.setCategoryGrade(getAverageReportScore(report.getId()));
        report.addReportScore(newReportScore); // Report 엔티티에 ReportScore 추가 (양방향 관계 설정)
        return new ReportResponseDto(report);
    }

    @Transactional(readOnly = true)
    public ReportResponseDto getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + reportId));
        return new ReportResponseDto(report);
    }

    @Transactional
    public List<ReportResponseDto> getAllReportsByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        List<Report> reports = reportRepository.findByUser(userRepository.findByUid(userId));

        return reports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponseDto.ReportScoreResponseDto getReportScoreById(Long reportId, Long scoreId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));

        ReportScore reportScore = report.getScoreLists().stream()
                .filter(score -> score.getId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ReportScore not found with id: " + scoreId + " in Report: " + reportId));

        return new ReportResponseDto.ReportScoreResponseDto(reportScore);
    }

    @Transactional
    public BigDecimal getAverageReportScore(Long reportId) {
        // 리포트 존재 여부 확인 로직
        // ...

        List<ReportScore> scores = reportScoreRepository.findByReportId(reportId);

        if (scores.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sum = scores.stream()
                .map(ReportScore::getScore)         // Stream<Integer>
                .map(BigDecimal::valueOf)           // 이 단계에서 Integer를 BigDecimal로 변환합니다.
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal count = BigDecimal.valueOf(scores.size());
        BigDecimal average = sum.divide(count, MathContext.DECIMAL64);

        // 3. 계산된 평균값의 소수점 자릿수를 2자리로 설정하고 반올림합니다.
        return average.setScale(2, RoundingMode.HALF_UP);
    }


    @Transactional
    public ReportResponseDto updateReport(Long reportId, ReportRequestDto reportRequestDto) {
        Report existingReport = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));
        existingReport.setCategoryName(reportRequestDto.getCategoryName());
        existingReport.setCategoryGrade(getAverageReportScore(reportId));
        existingReport.setTerm(reportRequestDto.getTerm());
        existingReport.setUserGrade(reportRequestDto.getUserGrade());
        Report updatedReport = reportRepository.save(existingReport);
        return convertToDto(updatedReport);
    }

    @Transactional
    public ReportResponseDto.ReportScoreResponseDto updateReportScore(Long reportScoreId, ReportRequestDto.ReportScoreRequestDto scoreRequestDto) {
        ReportScore existingReportScore = reportScoreRepository.findById(reportScoreId)
                .orElseThrow(() -> new IllegalArgumentException("ReportScore not found with id: " + reportScoreId));
        existingReportScore.setSubject(scoreRequestDto.getSubject());
        existingReportScore.setGrade(scoreRequestDto.getGrade());
        existingReportScore.setAchievement(scoreRequestDto.getAchievement());
        existingReportScore.setRanking(scoreRequestDto.getRanking());
        existingReportScore.setStudentNum(scoreRequestDto.getStudentNum());
        existingReportScore.setStandardDeviation(scoreRequestDto.getStandardDeviation());
        existingReportScore.setSubjectAverage(scoreRequestDto.getSubjectAverage());
        existingReportScore.setScore(scoreRequestDto.getScore());
        existingReportScore.setCredit(scoreRequestDto.getCredit());
        ReportScore updatedReportScore = reportScoreRepository.save(existingReportScore);
        return convertToDto(updatedReportScore);
    }

    @Transactional
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    private ReportResponseDto convertToDto(Report report) {
        // ReportResponseDto의 생성자를 호출하여 엔티티를 넘겨줍니다.
        return new ReportResponseDto(report);
    }
    private ReportResponseDto.ReportScoreResponseDto convertToDto(ReportScore reportScore) {
        // ReportResponseDto의 생성자를 호출하여 엔티티를 넘겨줍니다.
        return new ReportResponseDto.ReportScoreResponseDto(reportScore);
    }
}