package com.blog.board.repository;

import com.blog.board.domain.Category;
import com.blog.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPublishedTrueOrderByCreatedDateDesc(Pageable pageable);

    Page<Post> findByCategoryAndPublishedTrueOrderByCreatedDateDesc(Category category, Pageable pageable);

    Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Page<Post> findByCategoryOrderByCreatedDateDesc(Category category, Pageable pageable);
}
