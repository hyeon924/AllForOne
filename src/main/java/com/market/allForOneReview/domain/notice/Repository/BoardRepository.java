package com.market.allForOneReview.domain.notice.Repository;


import com.market.allForOneReview.domain.notice.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByBoardType(String boardType); // 메서드 이름 수정
}
