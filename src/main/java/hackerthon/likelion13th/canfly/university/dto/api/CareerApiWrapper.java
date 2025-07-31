package hackerthon.likelion13th.canfly.university.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CareerApiWrapper {

    private DataSearch dataSearch;

    /* ------------- (1) dataSearch ------------- */
    @Data
    public static class DataSearch {
        /** 실제 응답은 배열이므로 List 지정 */
        private List<Detail> content;
    }

    /* ------------- (2) content ------------- */
    @Data
    public static class Detail {
        /** 대학 목록이 바로 university 배열로 들어옴 */
        private List<UniversityInfo> university;
    }

    /* ------------- (3) university ------------- */
    @Data
    public static class UniversityInfo {
        private String schoolName;
    }
}
