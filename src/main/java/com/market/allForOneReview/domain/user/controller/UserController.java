package com.market.allForOneReview.domain.user.controller;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.domain.email.dto.EmailDTO;
import com.market.allForOneReview.domain.user.dto.UserCreateForm;
import com.market.allForOneReview.domain.user.dto.PasswordResetRequest;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;  // EmailService 대신 AuthService 주입

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/membership")
    public String signup(UserCreateForm userCreateForm) {
        return "member/membership";
    }

    @PostMapping("/membership")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Model model,
                         HttpSession session) throws MessagingException {
        if (!userCreateForm.isAgreement()) {
            bindingResult.rejectValue("agreement", "required", "이용약관에 동의해주세요.");
            return "member/membership";
        }
        if (bindingResult.hasErrors()) {
            return "member/membership";
        }

        // 기존의 validation 체크들...
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "member/membership";
        }

        if (userService.existsByUsername(userCreateForm.getUsername())) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자 ID 입니다.");
            return "member/membership";
        }

        if (userService.existsByEmail(userCreateForm.getEmail())) {
            bindingResult.reject("signupFailed", "이미 등록된 이메일 입니다.");
            return "member/membership";
        }

        if (userService.existsByNickname(userCreateForm.getNickname())) {
            bindingResult.reject("signupFailed", "중복된 닉네임 입니다.");
            return "member/membership";
        }

        try {
            // 1. 유저 생성
            SiteUser user = userService.create(
                    userCreateForm.getUsername(),
                    userCreateForm.getNickname(),
                    userCreateForm.getPassword1(),
                    userCreateForm.getEmail()
            );

            // 2. 인증 코드 생성 및 이메일 발송
            authService.sendAuthCode(user.getEmail());

            // 3. 저장된 인증 코드 가져오기
            String storedAuthCode = authService.getStoredAuthCode(user.getEmail());

            // 4. 데이터베이스에 인증 코드 저장
            userService.setVerificationCode(user.getEmail(), storedAuthCode);

            // 5. 세션에 이메일 저장 추가
            session.setAttribute("pendingEmail", user.getEmail());

            // 6. 인증 페이지로 필요한 정보 전달
            model.addAttribute("email", user.getEmail());

            log.info("User created and verification code sent to: {}", user.getEmail());

            return "auth/auth";

        } catch (DataIntegrityViolationException e) {
            log.error("회원가입 중 데이터 무결성 위반 오류 발생", e);
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "member/membership";
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            bindingResult.reject("signupFailed", e.getMessage());
            return "member/membership";
        }
    }

    // 아이디/비밀번호 찾기 페이지 보여주기
    @GetMapping("/find-account")
    public String findAccount() {
        return "auth/find_account";
    }

    @PostMapping("/find-id")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findId(@RequestBody EmailDTO emailDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = userService.findIdByEmail(emailDto.getEmail());
            response.put("success", true);
            response.put("username", username);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            response.put("success", false);
            response.put("message", "해당 이메일로 등록된 계정이 없습니다.");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = request.get("username");
            String email = request.get("email");

            // 사용자 정보 확인
            SiteUser user = userService.findByUsername(username);
            if (!user.getEmail().equals(email)) {
                throw new IllegalArgumentException("이메일 주소가 일치하지 않습니다.");
            }

            // 비밀번호 재설정 이메일 발송
            authService.sendPasswordResetEmail(email);

            response.put("success", true);
            response.put("message", "비밀번호 재설정 링크가 이메일로 발송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to process password reset request", e);
            response.put("success", false);
            response.put("message", "비밀번호 재설정 이메일 발송에 실패했습니다.");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/reset-password/{token}")
    public String showResetPasswordForm(@PathVariable("token") String token, Model model) {
        log.debug("Showing reset password form for token: {}", token);

        if (authService.isValidPasswordResetToken(token)) {
            model.addAttribute("token", token);
            return "auth/reset_password";  // 이 경로가 정확해야 함
        }

        return "redirect:/user/find-account?error=invalid_token";
    }

    // 비밀번호 재설정 처리
    @PostMapping("/reset-password/{token}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processResetPassword(
            @PathVariable("token") String token,
            @RequestBody PasswordResetRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            log.debug("Processing password reset for token: {}", token);

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            userService.resetPassword(token, request.getNewPassword());

            response.put("success", true);
            response.put("message", "비밀번호가 성공적으로 변경되었습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Password reset failed: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during password reset", e);
            response.put("success", false);
            response.put("message", "비밀번호 재설정 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}