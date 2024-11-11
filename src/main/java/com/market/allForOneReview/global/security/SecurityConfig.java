package com.market.allForOneReview.global.security;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.global.exception.EmailNotVerifiedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired  // 클래스 레벨에서 주입
    private AuthService authService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                )
                .formLogin((formLogin) -> formLogin
                        .loginPage("/user/login")
                        .failureHandler(authenticationFailureHandler())
                        .defaultSuccessUrl("/")
                )
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/auth/**")
                        )
                )
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            // 실제 원인 예외 가져오기
            Throwable cause = exception.getCause();

            if (cause instanceof EmailNotVerifiedException) {
                EmailNotVerifiedException ex = (EmailNotVerifiedException) cause;
                String email = ex.getEmail();

                try {
                    //이메일 발송.
                    authService.sendAuthCode(email);
                    //발송 후 인증 페이지로 이동.
                    String targetUrl = String.format("/auth?email=%s&message=%s",
                            URLEncoder.encode(email, StandardCharsets.UTF_8),
                            URLEncoder.encode("이메일 미인증 대상자입니다. 인증 이메일을 발송했습니다.", StandardCharsets.UTF_8));

                    response.sendRedirect(targetUrl);
                } catch (MessagingException e) {
                    response.sendRedirect("/user/login?error=email_send_failed");
                }
            } else {
                response.sendRedirect("/user/login?error=true");
            }
        };
    }
}