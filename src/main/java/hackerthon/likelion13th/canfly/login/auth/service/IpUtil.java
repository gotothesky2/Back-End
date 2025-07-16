package hackerthon.likelion13th.canfly.login.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

// 클라이언트 IP 추출
public class IpUtil {
    // HTTP 요청에서 클라이언트의 IP 주소를 추출
    public static String getClientIp(HttpServletRequest request) {
        String clientIp = null;
        boolean isIpInHeader = false;
        List<String> headerList = new ArrayList<>();
        headerList.add("X-Forwarded-For"); // 프록시 서버를 거쳐온 IP
        headerList.add("HTTP_CLIENT_IP"); // 클라이언트 IP
        headerList.add("HTTP_X_FORWARDED_FOR"); // 여러 프록시 서버를 거친 IP 목록
        headerList.add("HTTP_X_FORWARDED");
        headerList.add("HTTP_FORWARDED_FOR");
        headerList.add("HTTP_FORWARDED");
        headerList.add("Proxy-Client-IP"); // Apache Web Server용
        headerList.add("WL-Proxy-Client-IP"); // WebLogic 서버용
        headerList.add("HTTP_VIA"); // 프록시 서버 정보를 나타내는 헤더
        headerList.add("IPV6_ADR"); // IPv6 주소용 헤더

        // 헤더를 순회하며 클라이언트 IP 확인
        for (String header : headerList) {
            clientIp = request.getHeader(header);
            if (StringUtils.hasText(clientIp) && !clientIp.equalsIgnoreCase("unknown")) {
                isIpInHeader = true; // 헤더에서 IP 발견
                break;
            }
        }

        // 헤더에 IP가 없으면 요청의 원격 주소 반환
        if (!isIpInHeader) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}