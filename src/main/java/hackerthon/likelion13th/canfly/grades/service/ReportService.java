package hackerthon.likelion13th.canfly.grades.service;


import hackerthon.likelion13th.canfly.domain.report.Report;
import hackerthon.likelion13th.canfly.domain.report.ReportScore;
import hackerthon.likelion13th.canfly.domain.user.User;
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
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportScoreRepository reportScoreRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportResponseDto createReport(String uid, ReportRequestDto reportRequestDto) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + uid));

        Optional<Report> existingReportOpt = reportRepository.findByUserAndUserGradeAndTermAndCategoryName(
                user,
                reportRequestDto.getUserGrade(),
                reportRequestDto.getTerm(),
                reportRequestDto.getCategoryName()
        );

        Report updateReport = null;
        if (existingReportOpt.isPresent()) {
            // Report 존재 시에는 수정
            updateReport = existingReportOpt.get();
            updateReport.getScoreLists().clear();

        } else {
            // Report 없으면 생성
            updateReport = Report.builder()
                    .user(user)
                    .userGrade(reportRequestDto.getUserGrade())
                    .term(reportRequestDto.getTerm())
                    .categoryName(reportRequestDto.getCategoryName())
                    .build();
            user.addReport(updateReport);
        }


        // ReportScoreRequestDto 리스트를 ReportScore 엔티티 리스트로 변환하여 추가
        if (reportRequestDto.getScoreLists() != null && !reportRequestDto.getScoreLists().isEmpty()) {
            for (ReportRequestDto.ReportScoreRequestDto scoreDto : reportRequestDto.getScoreLists()) {
                ReportScore reportScore = ReportScore.builder()
                        .subject(scoreDto.getSubject())
                        .grade(scoreDto.getGrade())
                        .achievement(scoreDto.getAchievement())
                        .studentNum(scoreDto.getStudentNum())
                        .standardDeviation(scoreDto.getStandardDeviation())
                        .subjectAverage(scoreDto.getSubjectAverage())
                        .score(scoreDto.getScore())
                        .credit(scoreDto.getCredit())
                        .build();
                updateReport.addReportScore(reportScore);
            }
        }
        updateReport.setCategoryGrade(calculateAverageFromList(updateReport.getScoreLists()));
        Report savedReport = reportRepository.save(updateReport);
        return new ReportResponseDto(savedReport);
    }

//    @Transactional
//    public ReportResponseDto addReportScoreToReport(Long reportId, ReportRequestDto.ReportScoreRequestDto scoreRequestDto) {
//        Report report = reportRepository.findById(reportId)
//                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));
//
//        // DTO 필드에서 ReportScore 엔티티 직접 생성
//        ReportScore newReportScore = ReportScore.builder()
//                .subject(scoreRequestDto.getSubject())
//                .grade(scoreRequestDto.getGrade())
//                .achievement(scoreRequestDto.getAchievement())
//                .studentNum(scoreRequestDto.getStudentNum())
//                .standardDeviation(scoreRequestDto.getStandardDeviation())
//                .subjectAverage(scoreRequestDto.getSubjectAverage())
//                .score(scoreRequestDto.getScore())
//                .credit(scoreRequestDto.getCredit())
//                .build();;
//        report.addReportScore(newReportScore); // Report 엔티티에 ReportScore 추가 (양방향 관계 설정)
//        report.setCategoryGrade(calculateAverageFromList(report.getScoreLists()));
//        reportScoreRepository.save(newReportScore);
//        return new ReportResponseDto(report);
//    }

    @Transactional(readOnly = true)
    public ReportResponseDto getReportById(Long reportId, String userId) {
        Report report = reportRepository.findByIdAndUser_Uid(reportId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Report for user not found with ID: " + reportId));
        return new ReportResponseDto(report);
    }

    @Transactional
    public List<ReportResponseDto> getAllReportsByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with uid: " + userId);
        }

        List<Report> reports = reportRepository.findByUser(userRepository.findByUid(userId).orElseThrow(() -> new IllegalArgumentException("User not found with uid: " + userId)));

        return reports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponseDto.ReportScoreResponseDto getReportScoreById(Long reportId, Long scoreId, String userId) {
        Report report = reportRepository.findByIdAndUser_Uid(reportId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Report for user not found with id: " + reportId));

        ReportScore reportScore = report.getScoreLists().stream()
                .filter(score -> score.getId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ReportScore not found with id: " + scoreId + " in Report: " + reportId));

        return new ReportResponseDto.ReportScoreResponseDto(reportScore);
    }

    private BigDecimal calculateAverageFromList(List<ReportScore> scores) {
        if (scores == null || scores.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        // 학점에 따른 등급 가중치 조정
        BigDecimal weightedSum = scores.stream()
                .map(score -> {
                    BigDecimal grade = BigDecimal.valueOf(score.getGrade());
                    BigDecimal credit = BigDecimal.valueOf(score.getCredit());
                    return grade.multiply(credit);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = scores.stream()
                .map(score -> BigDecimal.valueOf(score.getCredit()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 0으로 나누는 경우 방지 (총 학점이 0일 경우)
        if (totalCredits.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal average = weightedSum.divide(totalCredits, MathContext.DECIMAL64);
        return average.setScale(2, RoundingMode.HALF_UP);
    }


//    @Transactional
//    public ReportResponseDto updateReport(Long reportId, ReportRequestDto reportRequestDto) {
//        Report existingReport = reportRepository.findById(reportId)
//                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));
//        existingReport.setCategoryName(reportRequestDto.getCategoryName());
//        Report updatedReport = reportRepository.save(existingReport);
//        return convertToDto(updatedReport);
//    }
//
//    @Transactional
//    public ReportResponseDto.ReportScoreResponseDto updateReportScore(Long reportScoreId, ReportRequestDto.ReportScoreRequestDto scoreRequestDto) {
//        ReportScore existingReportScore = reportScoreRepository.findById(reportScoreId)
//                .orElseThrow(() -> new IllegalArgumentException("ReportScore not found with id: " + reportScoreId));
//        existingReportScore.setSubject(scoreRequestDto.getSubject());
//        existingReportScore.setGrade(scoreRequestDto.getGrade());
//        existingReportScore.setAchievement(scoreRequestDto.getAchievement());
//        existingReportScore.setStudentNum(scoreRequestDto.getStudentNum());
//        existingReportScore.setStandardDeviation(scoreRequestDto.getStandardDeviation());
//        existingReportScore.setSubjectAverage(scoreRequestDto.getSubjectAverage());
//        existingReportScore.setScore(scoreRequestDto.getScore());
//        existingReportScore.setCredit(scoreRequestDto.getCredit());
//        Report parentReport = existingReportScore.getReport(); // ReportScore가 Report 참조를 가지고 있어야 함
//        if (parentReport != null) {
//            parentReport.setCategoryGrade(calculateAverageFromList(parentReport.getScoreLists()));
//        }
//        ReportScore updatedReportScore = reportScoreRepository.save(existingReportScore);
//        return convertToDto(updatedReportScore);
//    }

    @Transactional
    public void deleteReport(Long reportId, String userId) {
        reportRepository.deleteByIdAndUser_Uid(reportId, userId);
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