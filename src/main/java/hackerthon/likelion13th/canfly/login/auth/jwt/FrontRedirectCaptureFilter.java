package hackerthon.likelion13th.canfly.login.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class FrontRedirectCaptureFilter extends OncePerRequestFilter {

    public static final String ATTR = "FRONT_REDIRECT_URI";
    public static final String PARAM = "front_redirect";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        // 인가 시작점으로 들어오는 요청에서만 캡처
        String path = request.getServletPath();  // ex) /oauth2/authorization/kakao
        if (path != null && path.startsWith("/oauth2/authorization/")) {
            String redirect = request.getParameter(PARAM);
            if (redirect == null) {
                // 혹시 프론트가 아직 redirect_uri를 쓰고 있다면 백업으로도 받자
                redirect = request.getParameter("redirect_uri");
            }
            if (redirect == null) {
                // 헤더로 보냈다면 이것도 백업
                redirect = request.getHeader("X-Front-Redirect");
            }
            if (redirect != null && !redirect.isBlank()) {
                HttpSession session = request.getSession(true);
                session.setAttribute(ATTR, redirect);
                log.info("[FRONT_REDIRECT] saved into session: {}", redirect);
            }
        }

        chain.doFilter(request, response);
    }
}