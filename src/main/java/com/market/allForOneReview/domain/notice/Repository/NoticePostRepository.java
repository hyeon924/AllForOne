package com.market.allForOneReview.domain.notice.Repository;

import com.market.allForOneReview.domain.notice.entity.NoticePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticePostRepository extends JpaRepository<NoticePost, Long> {
    // Board의 boardType에 따라 NoticePost를 찾는 메소드
    Page<NoticePost> findByBoard_BoardType(String boardType, Pageable pageable);

    // 제목으로 검색하며 페이징 기능 추가
    Page<NoticePost> findByTitleContainingAndBoard_BoardType(String title, String boardType, Pageable pageable);

    // 내용으로 검색하며 페이징 기능 추가
    Page<NoticePost> findByContentContainingAndBoard_BoardType(String content, String boardType, Pageable pageable);
}
