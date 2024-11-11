package com.market.allForOneReview.domain.article.Repository;

import com.market.allForOneReview.domain.article.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryAndSubCategory(String category, String subCategory);
}
