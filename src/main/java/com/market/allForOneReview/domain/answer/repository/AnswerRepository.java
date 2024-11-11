package com.market.allForOneReview.domain.answer.repository;

import com.market.allForOneReview.domain.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
