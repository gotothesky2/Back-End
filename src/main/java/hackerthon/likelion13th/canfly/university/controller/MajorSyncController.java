package hackerthon.likelion13th.canfly.university.controller;

import hackerthon.likelion13th.canfly.domain.major.Major;
import hackerthon.likelion13th.canfly.global.api.ApiResponse;
import hackerthon.likelion13th.canfly.global.api.SuccessCode;
import hackerthon.likelion13th.canfly.search.repository.MajorRepository;
import hackerthon.likelion13th.canfly.university.service.UnivMajorSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "대학-전공 동기화", description = "외부 MAJOR_VIEW API를 호출해 대학·전공 정보를 저장")
@RestController
@RequestMapping("/major-sync")
@RequiredArgsConstructor
@Slf4j
public class MajorSyncController {

    private final UnivMajorSyncService syncService;
    private final MajorRepository majorRepo;

    @PostMapping("/all")
    @Operation(summary = "DB에 저장된 모든 전공 코드 자동 동기화")
    public ApiResponse<Integer> syncAllMajors() {
        List<Integer> allCodes = majorRepo.findAll()          // DB 조회
                .stream()
                .map(Major::getCode)
                .toList();
        allCodes.forEach(syncService::syncOne);
        return ApiResponse.onSuccess(SuccessCode.MAJORSYNC_BULK_SUCCESS, allCodes.size());
    }
}