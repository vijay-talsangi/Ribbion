package com.mpj.ribbion.repository;

import com.mpj.ribbion.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByQuestionIdOrderByCreatedAtAsc(Long questionId, Pageable pageable);
    Page<Comment> findByAnswerIdOrderByCreatedAtAsc(Long answerId, Pageable pageable);
}
