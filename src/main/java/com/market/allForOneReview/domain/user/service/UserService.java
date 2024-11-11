package com.market.allForOneReview.domain.user.service;

import com.market.allForOneReview.domain.user.entity.PasswordResetToken;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.repository.PasswordResetTokenRepository;
import com.market.allForOneReview.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public SiteUser create(String username, String nickname, String password, String email) {
        log.debug("Creating new user with email: {}", email);
        SiteUser user = SiteUser.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .email(email)
                .verified(false)
                .enabled(false)
                .authority(1)
                .verificationCode(null)
                .build();

        SiteUser savedUser = userRepository.save(user);
        log.info("Created new user with email: {}", email);
        return savedUser;
    }

    @Transactional
    public SiteUser save(SiteUser user) {
        log.debug("Saving user with email: {}, verified: {}, enabled: {}",
                user.getEmail(), user.isVerified(), user.isEnabled());
        SiteUser savedUser = userRepository.save(user);
        log.info("Successfully saved user: {}", user.getEmail());
        return savedUser;
    }

    public boolean existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        log.debug("Checking if username exists: {} - {}", username, exists);
        return exists;
    }

    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.debug("Checking if email exists: {} - {}", email, exists);
        return exists;
    }

    public boolean existsByNickname(String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        log.debug("Checking if nickname exists: {} - {}", nickname, exists);
        return exists;
    }

    public SiteUser findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                });
    }

    public SiteUser findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });
    }

    @Transactional
    public void verifyUser(String email, String verificationCode) {
        log.debug("Attempting to verify user with email: {} and code: {}", email, verificationCode);
        SiteUser user = findByEmail(email);

        if (verificationCode.equals(user.getVerificationCode())) {
            user.setVerified(true);
            user.setEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            log.info("Successfully verified user: {}", email);
        } else {
            log.warn("Invalid verification code for user: {}", email);
            throw new IllegalArgumentException("잘못된 인증 코드입니다.");
        }
    }

    @Transactional
    public void setVerificationCode(String email, String verificationCode) {
        log.debug("Setting verification code for user: {}", email);
        SiteUser user = findByEmail(email);
        user.setVerificationCode(verificationCode);
        userRepository.save(user);
        log.info("Verification code set for user: {}", email);
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        log.debug("Changing password for user: {}", username);
        SiteUser user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", username);
    }

    @Transactional
    public void changeNickname(String username, String newNickname) {
        log.debug("Attempting to change nickname for user: {} to {}", username, newNickname);
        if (existsByNickname(newNickname)) {
            log.warn("Nickname {} is already in use", newNickname);
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        SiteUser user = findByUsername(username);
        user.setNickname(newNickname);
        userRepository.save(user);
        log.info("Nickname changed successfully for user: {}", username);
    }

    @Transactional
    public void disableAccount(String username) {
        log.debug("Disabling account for user: {}", username);
        SiteUser user = findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
        log.info("Account disabled for user: {}", username);
    }

    @Transactional
    public void enableAccount(String username) {
        log.debug("Enabling account for user: {}", username);
        SiteUser user = findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
        log.info("Account enabled for user: {}", username);
    }

    public boolean checkPassword(String username, String password) {
        log.debug("Checking password for user: {}", username);
        SiteUser user = findByUsername(username);
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        log.debug("Password check result for user {}: {}", username, matches);
        return matches;
    }

    // 이메일로 아이디 찾기
    public String findIdByEmail(String email) {
        log.debug("Finding user ID by email: {}", email);
        SiteUser user = findByEmail(email);
        log.info("User ID found for email: {}", email);
        return user.getUsername();
    }

    @Transactional
    public void updatePassword(String email, String newPassword) {
        log.debug("Updating password for user with email: {}", email);
        SiteUser user = findByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user: {}", email);
    }

    @Transactional
    public void savePasswordResetToken(SiteUser user, String token) {
        log.debug("Saving password reset token for user: {}", user.getEmail());
        // 기존 토큰 삭제
        passwordResetTokenRepository.deleteByUser(user);

        // 새 토큰 저장
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
        log.info("Password reset token saved for user: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        SiteUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 사용된 토큰 삭제
        passwordResetTokenRepository.delete(resetToken);

        log.info("Password reset successful for user: {}", user.getUsername());
    }
}