package com.blog.board.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name ="posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name="title", nullable = false)
    private String title;

    @Lob
    @Column(name="content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="published", nullable = false)
    private boolean published;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // 게시글 수정 메서드
    public void update(String title, String content, boolean published, Category category) {
        this.title = title;
        this.content = content;
        this.published = published;
        this.category = category;
    }

    // 카테고리 연관관계 편의 메서드
    public void setCategory(Category category) {
        this.category = category;
        if (category != null && !category.getPosts().contains(this)) {
            category.getPosts().add(this);
        }
    }
}
