package com.market.allForOneReview.domain.auth.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthInfo {
    private final String code;
    private final LocalDateTime expiryTime;
    private final String email;

    public AuthInfo(String code, String email, int expirationMinutes) {
        this.code = code;
        this.email = email;
        // 밀리초가 아닌 분 단위로 수정
        this.expiryTime = LocalDateTime.now().plusMinutes(expirationMinutes);
    }

    public boolean isValid(String inputCode) {
        return !isExpired() && code.equals(inputCode);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    @Override
    public String toString() {
        return String.format("AuthInfo(email=%s, expiryTime=%s)",
                email, expiryTime);
    }
}