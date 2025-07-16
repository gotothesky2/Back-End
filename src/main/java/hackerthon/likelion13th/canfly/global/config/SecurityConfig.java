package hackerthon.likelion13th.canfly.global.config;

import hackerthon.likelion13th.canfly.login.auth.jwt.AuthCreationFilter;
import hackerthon.likelion13th.canfly.login.auth.jwt.JwtValidationFilter;
import hackerthon.likelion13th.canfly.login.auth.utils.OAuth2SuccessHandler;
import hackerthon.likelion13th.canfly.login.auth.utils.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final AuthCreationFilter authCreationFilter;
    private final JwtValidationFilter jwtValidationFilter;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔹 CORS 설정 적용
                .cors(withDefaults())
                // 🔹 CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 🔹 인증 및 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health", // health check
                                "/swagger-ui/**",         // 🔑 Swagger
                                "/v3/api-docs/**",
                                "/oauth2/authorization/kakao",
                                "/login/oauth2/**",        // 🟡  OAuth 콜백
                                "/token/**",              // 🔑 토큰 재발급 및 생성
                                "/oauth2/**",             // 🟡 카카오 OAuth 리디렉션
                                "/token/return",
                                "/index.html",
                                "/users/me",
                                "/users/logout"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 🔹 세션 정책: STATELESS (JWT 기반)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔹 OAuth2 로그인 설정 (UserService 연동)
                .oauth2Login(oauth2 -> oauth2
                        //.loginPage("/users/login")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                )

                // 🔹 필터 체인 적용
                .addFilterBefore(authCreationFilter, AnonymousAuthenticationFilter.class)
                .addFilterBefore(jwtValidationFilter, AuthCreationFilter.class);


        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "http://sajang-dev.ap-northeast-2.elasticbeanstalk.com",
                "https://likelionshop.netlify.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}