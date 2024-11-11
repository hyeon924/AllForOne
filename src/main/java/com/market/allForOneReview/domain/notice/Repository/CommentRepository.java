package com.market.allForOneReview.domain.notice.Repository;

import com.market.allForOneReview.domain.notice.entity.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<NoticeComment,Long> {
}
