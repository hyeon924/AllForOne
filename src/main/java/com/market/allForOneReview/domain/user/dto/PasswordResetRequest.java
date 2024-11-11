package com.market.allForOneReview.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class PasswordResetRequest {
    @NotBlank(message = "새 비밀번호를 입력해주세요")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인을 입력해주세요")
    private String confirmPassword;
}