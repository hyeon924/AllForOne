package com.market.allForOneReview.domain.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_type", nullable = false, unique = true)
    private String boardType;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<NoticePost> posts;

    public Board(){}

    public Board(String boardType) {
        this.boardType = boardType;
    }

}
