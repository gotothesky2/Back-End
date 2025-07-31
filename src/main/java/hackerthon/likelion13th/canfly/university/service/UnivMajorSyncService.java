package hackerthon.likelion13th.canfly.university.service;

import hackerthon.likelion13th.canfly.domain.major.Major;
import hackerthon.likelion13th.canfly.domain.university.University;
import hackerthon.likelion13th.canfly.search.repository.MajorRepository;
import hackerthon.likelion13th.canfly.university.dto.api.CareerApiWrapper;
import hackerthon.likelion13th.canfly.university.repository.UniversityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UnivMajorSyncService {

    private final WebClient.Builder builder;
    private final MajorRepository   majorRepo;
    private final UniversityRepository universityRepo;

    @Value("${career.base-url}")
    private String baseUrl;

    @Value("${career.api-key}")
    private String apiKey;

    // majorSeq 하나 ↔ 대학들 매핑 저장
    @Transactional
    public void syncOne(int majorSeq) {

        /* 1) Major 엔티티 */
        Major major = majorRepo.findByCode(majorSeq)
                .orElseThrow(() -> new IllegalArgumentException("Unknown m_code : " + majorSeq));

        /* 2) WebClient 호출 */
        WebClient web = builder.baseUrl(baseUrl).build();

        CareerApiWrapper apiRes = web.get()
                .uri(uri -> uri.path("/getOpenApi")
                        .queryParam("apiKey", apiKey)
                        .queryParam("svcType", "api")
                        .queryParam("svcCode", "MAJOR_VIEW")
                        .queryParam("contentType", "json")
                        .queryParam("gubun", "univ_list")
                        .queryParam("majorSeq", majorSeq)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CareerApiWrapper>() {})
                .block();

        /* 3) 대학 upsert + 조인 */
        if (apiRes == null || apiRes.getDataSearch() == null) return;

        apiRes.getDataSearch().getContent()
                .stream()
                .filter(Objects::nonNull)
                .filter(d -> d.getUniversity() != null)
                .flatMap(d -> d.getUniversity().stream())
                .map(u -> u.getSchoolName() == null ? "" : u.getSchoolName().trim())
                .filter(name -> !name.isBlank())
                .forEach(name -> {
                    University uni = universityRepo.findByName(name)
                            .orElseGet(() -> universityRepo.save(
                                    University.builder().name(name).build()));

                    /* 중복 연결 방지 */
                    if (!uni.getMajors().contains(major)) {
                        uni.getMajors().add(major);
                    }
                });
    }
}