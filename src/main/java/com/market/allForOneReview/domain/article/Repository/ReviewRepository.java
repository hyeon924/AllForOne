package com.market.allForOneReview.domain.article.Repository;

import com.market.allForOneReview.domain.article.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 카테고리로 검색
    Page<Review> findByCategory_Category(String category, Pageable pageable);

    // 카테고리와 서브카테고리로 검색
    Page<Review> findByCategory_CategoryAndCategory_SubCategory(String category, String subCategory, Pageable pageable);

    // 제목으로 검색
    Page<Review> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 카테고리와 제목으로 검색 (전체 서브카테고리)
    Page<Review> findByCategory_CategoryAndTitleContainingIgnoreCase(String category, String title, Pageable pageable);

    // 카테고리와 모든 필드로 검색 (전체 서브카테고리)
    @Query("SELECT r FROM Review r WHERE " +
            "LOWER(r.category.category) = LOWER(:category) AND (" +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Review> findByCategory_CategoryAndAllFieldsContainingIgnoreCase(
            @Param("category") String category,
            @Param("query") String query,
            Pageable pageable);

    // 카테고리와 서브카테고리, 제목으로 검색 (특정 서브카테고리)
    Page<Review> findByCategory_CategoryAndCategory_SubCategoryAndTitleContainingIgnoreCase(
            String category, String subCategory, String title, Pageable pageable);

    // 카테고리와 서브카테고리, 모든 필드로 검색 (특정 서브카테고리)
    @Query("SELECT r FROM Review r WHERE " +
            "LOWER(r.category.category) = LOWER(:category) AND LOWER(r.category.subCategory) = LOWER(:subCategory) AND (" +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Review> findByCategory_CategoryAndCategory_SubCategoryAndAllFieldsContainingIgnoreCase(
            @Param("category") String category,
            @Param("subCategory") String subCategory,
            @Param("query") String query,
            Pageable pageable);
}
