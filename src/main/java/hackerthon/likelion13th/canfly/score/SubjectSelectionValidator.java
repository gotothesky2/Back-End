package hackerthon.likelion13th.canfly.score;

import hackerthon.likelion13th.canfly.score.ScoreExcelEngine.SubjectInput;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static hackerthon.likelion13th.canfly.score.SubjectRegistry.*;

@Component
public class SubjectSelectionValidator {

    /** 유저 입력(라벨, 값)을 검증/정규화해서 엔진에 넣을 수 있는 리스트로 변환 */
    public ValidatedSelection validateToEngineInputs(List<SubjectInput> rawInputs) {
        if (rawInputs == null) rawInputs = List.of();

        // 1) 라벨 표준화 및 중복 한 번만 사용
        Map<Subject, Object> picked = new LinkedHashMap<>();
        for (SubjectInput in : rawInputs) {
            if (in == null || in.label() == null) continue;
            Subject subj = SubjectRegistry.byLabel(in.label());
            picked.put(subj, in.value()); // 중복 라벨은 마지막 값으로 덮음
        }

        // 2) 값 타입 및 범위 체크
        for (Map.Entry<Subject, Object> e : picked.entrySet()) {
            Subject s = e.getKey();
            Object v = e.getValue();
            if (!(v instanceof Number)) {
                throw new IllegalArgumentException("과목 '" + s.label() + "' 값은 숫자여야 합니다. (입력: " + v + ")");
            }
            Number n = (Number) v;
            if (s.getInputType() == InputType.SCORE && !isValidScore(n)) {
                throw new IllegalArgumentException("표준점수 범위 오류 '" + s.label() + "': " + n);
            }
            if (s.getInputType() == InputType.GRADE && !isValidGrade(n)) {
                throw new IllegalArgumentException("등급 범위 오류 '" + s.label() + "': " + n);
            }
        }

        // 3) 조합 규칙 검증
        // 3-1) 수학 3택1
        long mathCount = picked.keySet().stream().filter(s -> s.getGroup() == Group.MATH).count();
        if (mathCount > 1) {
            throw new IllegalArgumentException("수학은 (미적/기하/확통) 중 정확히 1개만 선택 가능합니다. (현재: " + mathCount + ")");
        }

        // 3-2) 과탐/사탐 합산 최대 2개
        List<Subject> sciSoc = picked.keySet().stream()
                .filter(s -> s.getGroup() == Group.SCIENCE || s.getGroup() == Group.SOCIAL)
                .collect(Collectors.toList());
        if (sciSoc.size() > 2) {
            throw new IllegalArgumentException("과탐/사탐 합산 최대 2과목까지 입력 가능합니다. (현재: " + sciSoc.size() + ")");
        }

        // 3-3) 제2외국어 최대 1개
        long secCount = picked.keySet().stream().filter(s -> s.getGroup() == Group.SECOND_LANG).count();
        if (secCount > 1) {
            throw new IllegalArgumentException("제2외국어는 최대 1과목만 입력 가능합니다. (현재: " + secCount + ")");
        }

        // 4) 엔진 입력으로 변환 (입력한 과목만)
        List<SubjectInput> engineInputs = picked.entrySet().stream()
                .map(e -> new SubjectInput(e.getKey().label(), e.getValue()))
                .collect(Collectors.toList());

        // 보조 정보(선택/조합 요약)도 함께 리턴하면 이후 저장 단계에서 유용
        SelectionSummary summary = summarize(picked.keySet());

        return new ValidatedSelection(engineInputs, summary);
    }

    private SelectionSummary summarize(Collection<Subject> picked) {
        String math = picked.stream().filter(s -> s.getGroup() == Group.MATH).map(Subject::label).findFirst().orElse(null);

        List<String> sciences = picked.stream().filter(s -> s.getGroup() == Group.SCIENCE).map(Subject::label).toList();
        List<String> socials  = picked.stream().filter(s -> s.getGroup() == Group.SOCIAL).map(Subject::label).toList();
        String secondLang = picked.stream().filter(s -> s.getGroup() == Group.SECOND_LANG).map(Subject::label).findFirst().orElse(null);

        return new SelectionSummary(math, sciences, socials, secondLang);
    }

    // ---- 반환 타입 ----
    public record ValidatedSelection(List<SubjectInput> engineInputs, SelectionSummary summary) {}

    @Getter
    public static class SelectionSummary {
        private final String math;            // 예: "수학(미적)" or null
        private final List<String> sciences;  // 선택된 과탐 라벨(0~2개)
        private final List<String> socials;   // 선택된 사탐 라벨(0~2개)
        private final String secondLang;      // 선택된 제2외국어 라벨 or null

        public SelectionSummary(String math, List<String> sciences, List<String> socials, String secondLang) {
            this.math = math;
            this.sciences = sciences != null ? List.copyOf(sciences) : List.of();
            this.socials = socials != null ? List.copyOf(socials) : List.of();
            this.secondLang = secondLang;
        }

        /** 과탐/사탐 합산 개수 */
        public int sciSocCount() { return sciences.size() + socials.size(); }
    }
}