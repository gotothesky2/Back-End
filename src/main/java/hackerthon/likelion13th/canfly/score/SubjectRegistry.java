package hackerthon.likelion13th.canfly.score;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/** 엑셀 B열의 과목 라벨과 입력유형(표준점수/등급), 그룹 정보를 상수화 */
public final class SubjectRegistry {

    private SubjectRegistry() {}

    public enum InputType { SCORE, GRADE }               // C열에 들어갈 값의 타입
    public enum Group { KOREAN, MATH, ENGLISH, HISTORY, SCIENCE, SOCIAL, SECOND_LANG }

    @Getter
    public enum Subject {
        // 국어/수학
        KOR("국어", Group.KOREAN, InputType.SCORE),
        MATH_MI("수학(미적)", Group.MATH, InputType.SCORE),
        MATH_GI("수학(기하)", Group.MATH, InputType.SCORE),
        MATH_ST("수학(확통)", Group.MATH, InputType.SCORE),

        // 영어/한국사 (등급 입력)
        ENG("영어", Group.ENGLISH, InputType.GRADE),
        HIST("한국사", Group.HISTORY, InputType.GRADE),

        // 과학탐구 (표준점수)
        SCI_PHY1("물리학 Ⅰ", Group.SCIENCE, InputType.SCORE),
        SCI_PHY2("물리학 Ⅱ", Group.SCIENCE, InputType.SCORE),
        SCI_BIO1("생명과학 Ⅰ", Group.SCIENCE, InputType.SCORE),
        SCI_BIO2("생명과학 Ⅱ", Group.SCIENCE, InputType.SCORE),
        SCI_EAR1("지구과학 Ⅰ", Group.SCIENCE, InputType.SCORE),
        SCI_EAR2("지구과학 Ⅱ", Group.SCIENCE, InputType.SCORE),
        SCI_CHE1("화학 Ⅰ", Group.SCIENCE, InputType.SCORE),
        SCI_CHE2("화학 Ⅱ", Group.SCIENCE, InputType.SCORE),

        // 사회탐구 (표준점수)
        SOC_ECON("경제", Group.SOCIAL, InputType.SCORE),
        SOC_EASIA("동아시아사", Group.SOCIAL, InputType.SCORE),
        SOC_SOC("사회·문화", Group.SOCIAL, InputType.SCORE),        // · (U+00B7)
        SOC_ETH("생활과 윤리", Group.SOCIAL, InputType.SCORE),
        SOC_WHIS("세계사", Group.SOCIAL, InputType.SCORE),
        SOC_GEO("세계지리", Group.SOCIAL, InputType.SCORE),
        SOC_PHIL("윤리와 사상", Group.SOCIAL, InputType.SCORE),
        SOC_POL("정치와 법", Group.SOCIAL, InputType.SCORE),
        SOC_KGEO("한국지리", Group.SOCIAL, InputType.SCORE),

        // 제2외국어 (등급 입력, 선택)
        SEC_GER("독일어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_RUS("러시아어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_VIE("베트남어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_SPA("스페인어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_ARB("아랍어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_JPN("일본어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_CHN("중국어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_FRE("프랑스어 Ⅰ", Group.SECOND_LANG, InputType.GRADE),
        SEC_HAN("한문 Ⅰ", Group.SECOND_LANG, InputType.GRADE);

        private final String label;        // 엑셀 B열의 텍스트와 정확히 일치
        private final Group group;
        private final InputType inputType;

        Subject(String label, Group group, InputType inputType) {
            this.label = label;
            this.group = group;
            this.inputType = inputType;
        }

        public String label() { return label; }
    }

    /** label → Subject 빠른 조회용 */
    private static final Map<String, Subject> BY_LABEL = Arrays.stream(Subject.values())
            .collect(Collectors.toMap(s -> normalize(s.label()), s -> s, (a,b)->a, LinkedHashMap::new));

    public static Subject byLabel(String label) {
        Subject s = BY_LABEL.get(normalize(label));
        if (s == null) throw new IllegalArgumentException("미등록 과목 라벨: " + label);
        return s;
    }

    public static boolean existsLabel(String label) {
        return BY_LABEL.containsKey(normalize(label));
    }

    /** 수학 3트랙 집합 */
    public static final Set<Subject> MATH_TRACKS = Set.of(Subject.MATH_MI, Subject.MATH_GI, Subject.MATH_ST);

    /** 입력 타입별 범위 기본값 (필요시 조정) */
    public static boolean isValidScore(Number n) {
        if (n == null) return false;
        int v = n.intValue();
        return (v >= 0 && v <= 200); // 표준점수 합리적 범위(유연)
    }
    public static boolean isValidGrade(Number n) {
        if (n == null) return false;
        int v = n.intValue();
        return (v >= 1 && v <= 9);   // 등급 1~9
    }

    /** 공백/비가시문자 정규화 */
    public static String normalize(String s) {
        if (s == null) return null;
        return s.replace('\u00A0', ' ').trim();
    }
}