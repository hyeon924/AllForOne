package com.market.allForOneReview.domain.auth.service;

import com.market.allForOneReview.domain.auth.dto.AuthInfo;
import com.market.allForOneReview.domain.email.service.EmailService;
import com.market.allForOneReview.domain.user.entity.PasswordResetToken;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.repository.PasswordResetTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private static final String AUTH_CODE_PREFIX = "AUTH:";

    @Value("${spring.auth.code.expiration-minutes}")
    private int authCodeExpirationMinutes;

    private final Map<String, AuthInfo> authCodeStore = new ConcurrentHashMap<>();

    @Transactional
    public void sendAuthCode(String email) throws MessagingException {
        String authCode = emailService.createAuthCode();
        String key = AUTH_CODE_PREFIX + email;

        // AuthInfo 생성자에 맞게 수정
        AuthInfo authInfo = new AuthInfo(authCode, email, authCodeExpirationMinutes);
        authCodeStore.put(key, authInfo);

        emailService.sendVerificationEmail(email, authCode, authCodeExpirationMinutes);
        log.info("Auth code sent to email: {}", email);
    }

    public boolean verifyEmail(String email, String authCode) {
        String key = AUTH_CODE_PREFIX + email;
        AuthInfo authInfo = authCodeStore.get(key);

        // 디버깅을 위한 로그 추가
        log.debug("Verifying email: {}, Provided code: {}, Stored AuthInfo: {}",
                email, authCode, authInfo);

        if (authInfo == null) {
            log.warn("No auth code found for email: {}", email);
            return false;
        }

        if (authInfo.isExpired()) {
            log.warn("Auth code expired for email: {}", email);
            authCodeStore.remove(key);
            return false;
        }

        boolean isValid = authInfo.isValid(authCode);
        if (isValid) {
            authCodeStore.remove(key);
            log.info("Email verified successfully: {}", email);
        } else {
            log.warn("Invalid auth code for email: {}", email);
        }

        return isValid;
    }

    @Transactional
    public void sendPasswordResetEmail(String email) throws MessagingException {
        // 1. 사용자 찾기
        SiteUser user = passwordResetTokenRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 2. 새 토큰 생성
        String resetToken = UUID.randomUUID().toString();

        // 3. 기존 토큰이 있다면 삭제
        passwordResetTokenRepository.deleteByUser(user);

        // 4. 새 토큰 생성 및 저장
        PasswordResetToken passwordResetToken = new PasswordResetToken(resetToken, user);
        passwordResetTokenRepository.save(passwordResetToken);

        // 5. 이메일 발송
        emailService.sendPasswordResetEmail(email, resetToken);

        log.info("Password reset token created and email sent to: {}", email);
    }

    @Transactional(readOnly = true)
    public boolean isValidPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isExpired())
                .orElse(false);
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void cleanupExpiredCodes() {
        int beforeSize = authCodeStore.size();
        authCodeStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int afterSize = authCodeStore.size();

        if (beforeSize != afterSize) {
            log.info("Cleaned up {} expired auth codes", beforeSize - afterSize);
        }
    }

    public String getStoredAuthCode(String email) {
        AuthInfo authInfo = authCodeStore.get(AUTH_CODE_PREFIX + email);
        // 디버깅을 위한 로그 추가
        log.debug("Retrieving stored auth code for email: {}, AuthInfo: {}", email, authInfo);
        return authInfo != null ? authInfo.getCode() : null;
    }


}