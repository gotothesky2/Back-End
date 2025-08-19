package hackerthon.likelion13th.canfly.score;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * 엑셀 템플릿(시트명: 수능입력)의 B열에서 과목 라벨을 찾아
 * 같은 행의 C열(입력)을 채우고, D/E/F열(백분위/등급/누적%)을 수식 평가하여 읽어오는 엔진.
 *
 * - 파일은 절대 영속 저장하지 않음(템플릿을 임시파일로 복사 후 파일 기반으로만 연다)
 * - 영어/한국사/제2외국어는 "등급"을 C열에 넣어도 됨 (엔진은 점수/등급을 구분하지 않음)
 * - 메모리 절약을 위해 InputStream 기반이 아닌 OPCPackage(파일 기반)으로 Workbook을 연다.
 */
@Service
public class ScoreExcelEngine {

    private static final String TEMPLATE_PATH = "/excel/score_template.xlsx"; // resources 기준
    private static final String SHEET_NAME = "수능입력";

    // 컬럼 인덱스(0-based)
    private static final int COL_SUBJECT = 1; // B열: 과목 라벨
    private static final int COL_INPUT   = 2; // C열: 표준점수 or 등급 입력
    private static final int COL_PCT     = 3; // D열: 백분위
    private static final int COL_GRADE   = 4; // E열: 등급
    private static final int COL_CUM     = 5; // F열: 누적 백분위(%)

    /** 템플릿을 임시파일로 복사해 두고, 매 요청마다 파일 기반으로 연다(힙 절약). */
    private final Path templateFile;

    /** 동시 실행 제한(폭주 시 메모리 보호). 시스템 프로퍼티 excel.permits 로 조절 가능(기본 8). */
    private final Semaphore excelPermits = new Semaphore(Integer.getInteger("excel.permits", 8));

    public ScoreExcelEngine() throws IOException {
        try (InputStream is = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (is == null) {
                throw new IllegalStateException("Excel template not found: " + TEMPLATE_PATH);
            }
            Path tmp = Files.createTempFile("score_template_", ".xlsx");
            Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
            this.templateFile = tmp;
            // 컨테이너 종료 시 정리
            tmp.toFile().deleteOnExit();
        }
    }

    /**
     * 여러 과목 입력을 한 번에 평가.
     * @param inputs 사용자 입력(과목라벨, 값)의 리스트
     * @return 과목라벨별 계산 결과(백분위/등급/누적%)
     */
    public Map<String, SubjectResult> evaluateAll(List<SubjectInput> inputs) {
        if (inputs == null || inputs.isEmpty()) return Collections.emptyMap();

        try {
            excelPermits.acquire();
            return doEvaluate(inputs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Excel 평가 대기 중 인터럽트", ie);
        } finally {
            excelPermits.release();
        }
    }

    private Map<String, SubjectResult> doEvaluate(List<SubjectInput> inputs) {
        // ✅ 파일 기반으로 열기: InputStream 경로는 메모리 폭주 위험
        try (OPCPackage pkg = OPCPackage.open(templateFile.toFile(), PackageAccess.READ_WRITE);
             Workbook wb = new XSSFWorkbook(pkg)) {

            Sheet sheet = Optional.ofNullable(wb.getSheet(SHEET_NAME))
                    .orElseThrow(() -> new IllegalStateException("시트를 찾을 수 없습니다: " + SHEET_NAME));

            // 1) 시트에서 B열 라벨 → 행번호 맵핑을 생성
            Map<String, Integer> labelToRow = buildLabelIndex(sheet);

            // 2) 입력값 주입(C열)
            for (SubjectInput in : inputs) {
                Integer rowIdx = labelToRow.get(normalize(in.label()));
                if (rowIdx == null) {
                    throw new IllegalArgumentException("엑셀에서 과목 라벨을 찾지 못했습니다: " + in.label());
                }
                Row row = getOrCreateRow(sheet, rowIdx);
                Cell c = getOrCreateCell(row, COL_INPUT);
                setNumericOrString(c, in.value());
            }

            // 3) 수식 평가 준비
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            evaluator.clearAllCachedResultValues();

            // 4) 결과 읽기(D/E/F열)
            Map<String, SubjectResult> result = new LinkedHashMap<>();
            for (SubjectInput in : inputs) {
                Integer rowIdx = labelToRow.get(normalize(in.label()));
                Row row = sheet.getRow(rowIdx);

                SubjectResult r = new SubjectResult(
                        evalAsInteger(row.getCell(COL_PCT), evaluator),   // 백분위
                        evalAsInteger(row.getCell(COL_GRADE), evaluator), // 등급
                        evalAsBigDecimal(row.getCell(COL_CUM), evaluator) // 누적%
                );
                result.put(in.label(), r);
            }
            return result;

        } catch (IOException e) {
            throw new IllegalStateException("Excel 평가 중 I/O 오류", e);
        } catch (InvalidFormatException e) {
            throw new IllegalStateException("Excel 포맷 오류", e);
        }
    }

    // ---- 내부 유틸 ----

    private static Map<String, Integer> buildLabelIndex(Sheet sheet) {
        Map<String, Integer> map = new HashMap<>();
        int last = sheet.getLastRowNum();
        for (int i = 0; i <= last; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell labelCell = row.getCell(COL_SUBJECT);
            String label = readAsString(labelCell);
            if (label == null || label.isBlank()) continue;
            map.put(normalize(label), i);
        }
        return map;
    }

    private static String normalize(String s) {
        if (s == null) return null;
        // 공백/전각스페이스 제거, 트림
        return s.replace('\u00A0', ' ').trim();
    }

    private static Row getOrCreateRow(Sheet sheet, int r) {
        Row row = sheet.getRow(r);
        return (row != null) ? row : sheet.createRow(r);
    }

    private static Cell getOrCreateCell(Row row, int c) {
        Cell cell = row.getCell(c);
        return (cell != null) ? cell : row.createCell(c);
    }

    private static void setNumericOrString(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        if (value instanceof Number n) {
            cell.setCellValue(n.doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static String readAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double d = cell.getNumericCellValue();
                if (d == Math.rint(d)) yield String.valueOf((long) d);
                else yield String.valueOf(d);
            }
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private static Integer evalAsInteger(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return null;
        CellValue cv = evaluator.evaluate(cell);
        if (cv == null) return null;
        return switch (cv.getCellType()) {
            case NUMERIC -> (int) Math.round(cv.getNumberValue());
            case STRING -> {
                try { yield Integer.parseInt(cv.getStringValue().trim()); }
                catch (Exception e) { yield null; }
            }
            case BOOLEAN -> cv.getBooleanValue() ? 1 : 0;
            default -> null;
        };
    }

    private static BigDecimal evalAsBigDecimal(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return null;
        CellValue cv = evaluator.evaluate(cell);
        if (cv == null) return null;
        return switch (cv.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cv.getNumberValue()).setScale(2, RoundingMode.HALF_UP);
            case STRING -> {
                try { yield new BigDecimal(cv.getStringValue().trim()).setScale(2, RoundingMode.HALF_UP); }
                catch (Exception e) { yield null; }
            }
            default -> null;
        };
    }

    // ==== 외부에서 쓰기 쉬운 입력/출력 타입(record) ====

    /** 사용자가 보낸 과목 입력(라벨=엑셀 B열 텍스트, value=표준점수 또는 등급) */
    public record SubjectInput(String label, Object value) {}

    /** 엑셀 계산 결과(백분위/등급/누적%) */
    public record SubjectResult(Integer percentile, Integer grade, BigDecimal cumulative) {}
}
