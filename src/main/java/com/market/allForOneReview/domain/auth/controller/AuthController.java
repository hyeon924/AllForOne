package com.market.allForOneReview.domain.auth.controller;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/auth")
    public String getAuthPage(@RequestParam(value = "error", required = false) Boolean error,
                              @RequestParam(value = "verified", required = false) Boolean verified,
                              @RequestParam(value = "email", required = false) String email,
                              @RequestParam(value = "authNumber", required = false) String authNumber,
                              Model model,
                              RedirectAttributes redirectAttributes) {  // RedirectAttributes 추가
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("errorMessage", "인증번호가 일치하지 않거나 만료되었습니다.");
        }
        if (Boolean.TRUE.equals(verified)) {
            model.addAttribute("successMessage", "이메일 인증이 완료되었습니다.");
        }
        if (email != null && !email.isEmpty()) {
            model.addAttribute("email", email);
        }
        if (authNumber != null && !authNumber.isEmpty()) {
            model.addAttribute("authNumber", authNumber);
            // 자동으로 인증 시도
            try {
                SiteUser user = userService.findByEmail(email);
                boolean isVerified = authService.verifyEmail(email, authNumber);

                if (isVerified) {
                    log.info("Email verification successful for: {}", email);
                    user.setVerified(true);
                    user.setEnabled(true);
                    userService.save(user);

                    redirectAttributes.addFlashAttribute("message", "이메일 인증이 완료되었습니다. 로그인해주세요.");
                    return "redirect:/user/login";
                }
            } catch (Exception e) {
                log.error("Auto verification failed for email: {} with auth number: {}", email, authNumber);
                model.addAttribute("error", "인증번호가 일치하지 않거나 만료되었습니다.");
            }
        }
        return "auth/auth";
    }

    @PostMapping("/auth")
    public String verifyEmail(@RequestParam(name = "email") String email,
                              @RequestParam(name = "authNumber") String authNumber,
                              Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        // 세션에서 이메일 가져오기
        String pendingEmail = (String) session.getAttribute("pendingEmail");
        if (email == null || email.trim().isEmpty()) {
            email = pendingEmail;
        }

        try {
            SiteUser user = userService.findByEmail(email);

            // 인증번호 검증
            boolean isVerified = authService.verifyEmail(email, authNumber);

            if (isVerified) {
                log.info("Email verification successful for: {}", email);
                user.setVerified(true);
                user.setEnabled(true);
                userService.save(user);

                // 디버깅을 위한 로그 추가
                log.info("User status updated - verified: {}, enabled: {}",
                        user.isVerified(), user.isEnabled());

                redirectAttributes.addFlashAttribute("message",
                        "이메일 인증이 완료되었습니다. 로그인해주세요.");
                return "redirect:/user/login";
            } else {
                log.warn("Email verification failed for: {} with auth number: {}",
                        email, authNumber);
                model.addAttribute("error", "인증번호가 일치하지 않거나 만료되었습니다.");
                return "auth/auth";
            }
        } catch (UsernameNotFoundException e) {
            log.error("User not found for email: {}", email);
            model.addAttribute("error", "등록되지 않은 이메일입니다.");
            return "auth/auth";
        } catch (Exception e) {
            log.error("Error during email verification: ", e);
            model.addAttribute("error", "인증 처리 중 오류가 발생했습니다.");
            return "auth/auth";
        }
    }

    @PostMapping("/auth/send")
    @ResponseBody
    public ResponseEntity<String> sendAuthCode(@RequestParam(name = "email") String email) {
        try {
            // 사용자 존재 여부 확인
            SiteUser user = userService.findByEmail(email);

            // 인증 코드 생성 및 이메일 발송
            authService.sendAuthCode(email);

            // 생성된 인증 코드를 데이터베이스에도 저장
            String storedAuthCode = authService.getStoredAuthCode(email);
            userService.setVerificationCode(email, storedAuthCode);

            log.info("Auth code sent and saved for user: {}", email);
            return ResponseEntity.ok("인증 코드가 발송되었습니다.");

        } catch (UsernameNotFoundException e) {
            log.warn("Attempt to send auth code to non-existent email: {}", email);
            return ResponseEntity.badRequest().body("등록되지 않은 이메일입니다.");

        } catch (Exception e) {
            log.error("Failed to send auth code to {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("인증 코드 발송에 실패했습니다.");
        }
    }
}