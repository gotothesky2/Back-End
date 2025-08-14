package hackerthon.likelion13th.canfly.global.config;

import hackerthon.likelion13th.canfly.login.auth.jwt.AuthCreationFilter;
import hackerthon.likelion13th.canfly.login.auth.jwt.FrontRedirectCaptureFilter;
import hackerthon.likelion13th.canfly.login.auth.jwt.JwtValidationFilter;
import hackerthon.likelion13th.canfly.login.auth.utils.OAuth2SuccessHandler;
import hackerthon.likelion13th.canfly.login.auth.utils.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthCreationFilter authCreationFilter;
    private final JwtValidationFilter jwtValidationFilter;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final FrontRedirectCaptureFilter frontRedirectCaptureFilter;

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
                                "/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",             // 🟡 카카오 OAuth 리디렉션
                                "/login/oauth2/**",        // 🟡 카카오 OAuth 콜백
                                "/token/**",
                                "/index.html",
                                "/users/me",
                                "/users/logout",
                                "/users/grades/mock"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(h -> h
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)    // iframe.html 차단 해제
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(frontRedirectCaptureFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(authCreationFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtValidationFilter, AuthCreationFilter.class);


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}