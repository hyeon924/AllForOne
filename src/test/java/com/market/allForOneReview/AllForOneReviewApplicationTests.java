package com.market.allForOneReview;

import com.market.allForOneReview.domain.article.Repository.ReviewRepository;
import com.market.allForOneReview.domain.article.entity.Review;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class AllForOneReviewApplicationTests {

	@Autowired
	private ReviewRepository reviewRepository;

	@Test
	void testFindByTitleContainingIgnoreCase() {

		Page<Review> result = reviewRepository.findByTitleContainingIgnoreCase("12", PageRequest.of(0, 10));

		// Then: 검색된 리뷰가 존재해야 함
		Assertions.assertFalse(result.isEmpty(), "검색 결과가 존재해야 합니다.");
		Assertions.assertEquals(1, result.getTotalElements(), "검색된 리뷰는 정확히 1개여야 합니다.");
		Assertions.assertEquals("12", result.getContent().get(0).getTitle(), "검색된 리뷰 제목은 '12'여야 합니다.");

	}

}