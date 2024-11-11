package com.market.allForOneReview.domain.notice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoticeForm {

    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String title;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    @NotEmpty(message = "탭을눌러 공지타입을 정해주세요")
    private String boardType;

}
