package com.market.allForOneReview.domain.answer.entity;

import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private Review review;

    @ManyToOne
    private SiteUser author;
}
