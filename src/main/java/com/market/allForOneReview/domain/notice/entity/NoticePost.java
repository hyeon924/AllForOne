package com.market.allForOneReview.domain.notice.entity;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class NoticePost extends BaseEntity {

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    private Integer views = 0;

    @OneToMany(mappedBy = "noticePost", cascade = CascadeType.REMOVE)
    private List<NoticeComment> comments;

    @ManyToOne
    private SiteUser author;

    public void incrementViews() {
        this.views++;
    }

}
