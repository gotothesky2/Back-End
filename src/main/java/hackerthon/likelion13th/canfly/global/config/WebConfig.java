package hackerthon.likelion13th.canfly.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 애플리케이션의 모든 API 경로에 대해 CORS 설정을 적용합니다.
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080", "http://localhost:3000", "http://127.0.0.1:3000", "https://can-fly2.netlify.app/") // 로컬 환경의 Swagger UI
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메서드를 지정합니다.
                .allowedHeaders("*") // 모든 HTTP 헤더를 허용합니다.
                .allowCredentials(true); // 쿠키 및 인증 정보를 포함한 요청을 허용합니다.
    }
}