package hackerthon.likelion13th.canfly.score;

import java.util.List;

import static hackerthon.likelion13th.canfly.score.SubjectRegistry.*;

public final class SubjectCategoryMapper {
    private SubjectCategoryMapper() {}

    /**
     * 라벨(=Subject)과 SCIENCE/SOCIAL 선택 순서를 기반으로 엔티티 category 코드(1~7) 계산
     * @param subject 선택된 Subject
     * @param indexInSciSoc SCIENCE/SOCIAL 묶음 내 선택 순서(0부터) — 첫 번째=5, 두 번째=6
     */
    public static int toCategoryCode(Subject subject, Integer indexInSciSoc) {
        return switch (subject.getGroup()) {
            case KOREAN -> 1;
            case MATH -> 2;
            case ENGLISH -> 3;
            case HISTORY -> 4;
            case SCIENCE, SOCIAL -> {
                // 탐구는 1~2개만 가능(3단계 검증으로 보장). 순서대로 5, 6 부여.
                if (indexInSciSoc == null) indexInSciSoc = 0;
                yield (indexInSciSoc == 0) ? 5 : 6;
            }
            case SECOND_LANG -> 7;
        };
    }

    /**
     * SCIENCE/SOCIAL 안에서 몇 번째인지 반환 (없으면 null) — 5단계에서 저장 시 사용
     */
    public static Integer sciSocIndex(Subject subject, List<Subject> pickedOrder) {
        if (subject.getGroup() != Group.SCIENCE && subject.getGroup() != Group.SOCIAL) return null;
        int idx = 0;
        for (Subject s : pickedOrder) {
            if (s.getGroup() == Group.SCIENCE || s.getGroup() == Group.SOCIAL) {
                if (s == subject) return idx;
                idx++;
            }
        }
        return null;
    }
}
