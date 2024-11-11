package com.market.allForOneReview.domain.article.Service;

import com.market.allForOneReview.global.exception.DataNotFoundException;
import com.market.allForOneReview.domain.article.Repository.CategoryRepository;
import com.market.allForOneReview.domain.article.Repository.ReviewRepository;
import com.market.allForOneReview.domain.article.entity.Category;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getList() {
        return this.reviewRepository.findAll();
    }

    public Review getReview(Long id) {
        Optional<Review> review = this.reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    //    게시글 저장
    private final CategoryRepository categoryRepository;
    public void create(String title, String contentStory, String content, String category, String subCategory, SiteUser user) {

        Optional<Category> category1 = this.categoryRepository.findByCategoryAndSubCategory(category,subCategory);

        Review r = new Review();
        r.setTitle(title);
        r.setContentStory(contentStory);
        r.setContent(content);
        r.setCreateDate(LocalDateTime.now());
        r.setCategory(category1.get());
        r.setAuthor(user);
        this.reviewRepository.save(r);
    }

    //    페이징
    public Page<Review> getReviewsByCategoryAndPage(String categoryName, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 10개씩 페이징 처리
        return reviewRepository.findByCategory_Category(categoryName, pageable);
    }

    public Page<Review> searchReviews(String filter, String query, String category, String subCategory, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        // 서브카테고리가 "all"인 경우, 전체 서브카테고리를 대상으로 검색
        if ("all".equalsIgnoreCase(subCategory)) {
            switch (filter) {
                case "title":
                    return reviewRepository.findByCategory_CategoryAndTitleContainingIgnoreCase(category, query, pageRequest);
//                case "author":
//                    return reviewRepository.findByCategory_CategoryAndUser_UsernameContainingIgnoreCase(category, query, pageRequest);
                default:
                    return reviewRepository.findByCategory_CategoryAndAllFieldsContainingIgnoreCase(category, query, pageRequest);
            }
        } else {
            // 특정 서브카테고리를 검색하는 경우
            switch (filter) {
                case "title":
                    return reviewRepository.findByCategory_CategoryAndCategory_SubCategoryAndTitleContainingIgnoreCase(category, subCategory, query, pageRequest);
//                case "author":
//                    return reviewRepository.findByCategory_CategoryAndCategory_SubCategoryAndUser_UsernameContainingIgnoreCase(category, subCategory, query, pageRequest);
                default:
                    return reviewRepository.findByCategory_CategoryAndCategory_SubCategoryAndAllFieldsContainingIgnoreCase(category, subCategory, query, pageRequest);
            }
        }
    }

    public void modify(Review review, String title, String contentStory, String content) {
        review.setTitle(title);
        review.setContentStory(contentStory);
        review.setContent(content);
        review.setModifiedDate(LocalDateTime.now());
        this.reviewRepository.save(review);
    }

    public void delete(Review review) {
        this.reviewRepository.delete(review);
    }

    public void vote(Review review, SiteUser siteUser) {
        review.getVoter().add(siteUser);
        this.reviewRepository.save(review);
    }
}
