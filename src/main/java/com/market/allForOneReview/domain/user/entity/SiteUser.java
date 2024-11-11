package com.market.allForOneReview.domain.user.entity;

import com.market.allForOneReview.domain.notice.entity.NoticePost;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser extends BaseEntity {
    @Column(length = 100, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column
    private int authority;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviewPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<NoticePost> noticePosts;

    private String verificationCode; // 인증 코드 저장

    private boolean verified = false;  // 이메일 인증 여부
    private boolean enabled = false;   // 계정 활성화 여부
}