package com.mpj.ribbion.repository;

import com.mpj.ribbion.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId ORDER BY a.accepted DESC, a.voteCount DESC, a.createdAt ASC")
    Page<Answer> findByQuestionIdSorted(@Param("questionId") Long questionId, Pageable pageable);

    Page<Answer> findByAuthorId(Long authorId, Pageable pageable);

    long countByQuestionId(Long questionId);
}
