package com.market.allForOneReview.domain.email.controller;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.domain.email.dto.EmailRequest;
import com.market.allForOneReview.domain.email.dto.EmailVerificationRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private final AuthService authService;  // EmailService 대신 AuthService 사용

    @PostMapping("/send-verification")
    public ResponseEntity<Void> sendVerificationEmail(
            @Valid @RequestBody EmailRequest request) {
        try {
            authService.sendAuthCode(request.getEmail());
            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            log.error("Failed to send verification email", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyEmail(
            @Valid @RequestBody EmailVerificationRequest request) {
        boolean isValid = authService.verifyEmail(
                request.getEmail(),
                request.getCode()
        );
        return ResponseEntity.ok(isValid);
    }
}