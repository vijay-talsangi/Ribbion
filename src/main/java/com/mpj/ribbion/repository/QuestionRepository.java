package com.mpj.ribbion.repository;

import com.mpj.ribbion.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Question> findAllByOrderByVoteCountDesc(Pageable pageable);

    Page<Question> findAllByOrderByViewCountDesc(Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.answerCount = 0 ORDER BY q.createdAt DESC")
    Page<Question> findUnanswered(Pageable pageable);

    @Query("SELECT q FROM Question q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.body) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t.name = :tagName")
    Page<Question> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    Page<Question> findByAuthorId(Long authorId, Pageable pageable);
}
