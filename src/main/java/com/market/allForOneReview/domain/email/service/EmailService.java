package com.market.allForOneReview.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;  // Thymeleaf 엔진 주입

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${server.port}")  // server.port 값을 주입
    private String serverPort;

    // 인증 코드 생성
    public String createAuthCode() {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int type = (int) (Math.random() * 3);
            switch (type) {
                case 0 -> key.append((char) ((int) (Math.random() * 26) + 97)); // 소문자
                case 1 -> key.append((char) ((int) (Math.random() * 26) + 65)); // 대문자
                case 2 -> key.append((int) (Math.random() * 10)); // 숫자
            }
        }
        log.debug("Generated auth code");
        return key.toString();
    }

    // 이메일 인증 메일 발송
    public void sendVerificationEmail(String recipientEmail, String authCode, int expirationMinutes) throws MessagingException {
        try {
            MimeMessage message = createVerificationEmail(recipientEmail, authCode, expirationMinutes);
            mailSender.send(message);
            log.info("Verification email sent to: {}", recipientEmail);
        } catch (MailException e) {
            log.error("Failed to send verification email to: {}", recipientEmail, e);
            throw new MailSendException("메일 발송 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 재설정 메일 발송
    public void sendPasswordResetEmail(String recipientEmail, String resetToken) throws MessagingException {
        try {
            MimeMessage message = createPasswordResetEmail(recipientEmail, resetToken);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", recipientEmail);
        } catch (MailException e) {
            log.error("Failed to send password reset email to: {}", recipientEmail, e);
            throw new MailSendException("비밀번호 재설정 메일 발송 중 오류가 발생했습니다.");
        }
    }

    // 인증 메일 생성
    private MimeMessage createVerificationEmail(String recipientEmail, String authCode, int expirationMinutes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("AllForReview 이메일 인증");

        // Thymeleaf Context 설정
        Context context = new Context();
        context.setVariable("authCode", authCode);
        context.setVariable("expirationMinutes", expirationMinutes);

        // 인증 화면으로 바로 이동하는 URL 생성 (인증 코드를 함께 전달)
        String authUrl = String.format("http://localhost:%s/auth?email=%s&authNumber=%s",
                serverPort,
                URLEncoder.encode(recipientEmail, StandardCharsets.UTF_8),
                URLEncoder.encode(authCode, StandardCharsets.UTF_8));
        context.setVariable("authUrl", authUrl);

        // 템플릿 처리
        String body = templateEngine.process("member/verification-email", context);
        helper.setText(body, true);
        return message;
    }

    // 비밀번호 재설정 메일 생성
    private MimeMessage createPasswordResetEmail(String recipientEmail, String resetToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("[AllForReview] 비밀번호 재설정");

        // URL 경로 수정
        String resetLink = "http://localhost:8080/user/reset-password/" + resetToken;  // 개발환경용
        String body = """
                <div style="margin: 20px; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #333;">비밀번호 재설정</h2>
                    <p style="font-size: 16px;">아래 링크를 클릭하여 비밀번호를 재설정하세요:</p>
                    <div style="margin: 20px 0;">
                        <a href="%s" 
                           style="background-color: #007bff; color: white; padding: 10px 20px; 
                                  text-decoration: none; border-radius: 5px;">
                            비밀번호 재설정하기
                        </a>
                    </div>
                    <p style="color: #666; font-size: 14px;">이 링크는 30분 동안 유효합니다.</p>
                    <p style="color: #666; font-size: 14px;">본인이 요청하지 않은 경우 이 이메일을 무시하시면 됩니다.</p>
                </div>
                """.formatted(resetLink);

        helper.setText(body, true);
        return message;
    }
}