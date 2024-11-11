package com.market.allForOneReview.domain.article.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String title;

    @NotEmpty(message="줄거리는 필수항목입니다.")
    private String contentStory;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    private String category;

    private String subCategory;

}
