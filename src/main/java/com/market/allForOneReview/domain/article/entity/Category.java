package com.market.allForOneReview.domain.article.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;


    @Column
    private String subCategory;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Review> reviews;

    // 기본 생성자
    public Category() {
    }

    public Category(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }
}
