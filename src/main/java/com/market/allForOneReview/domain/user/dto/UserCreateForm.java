package com.market.allForOneReview.domain.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    @NotEmpty(message = "아이디는 필수입력 사항입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "아이디는 영문 대소문자와 숫자만 사용하여 4 ~ 20자리여야 합니다.")
    private String username;

    @NotEmpty(message = "닉네임을 필수입력 사항입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{3,10}$", message = "닉네임은 한글, 영문, 숫자만 사용하여 3 ~ 10자리여야 합니다.")
    private String nickname;

    @NotEmpty(message = "비밀번호는 필수입력 사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\w\\S])[A-Za-z\\d\\w\\S]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8 ~ 20자리여야 합니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수입력 사항입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수입력 사항입니다.")
    @Email
    private String email;

    @AssertTrue(message = "이용약관에 동의해주세요.")
    private boolean agreement;
}