package com.mpj.ribbion.repository;

import com.mpj.ribbion.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);
    List<Tag> findByNameInIgnoreCase(Set<String> names);

    @Query("SELECT t FROM Tag t ORDER BY t.questionCount DESC")
    Page<Tag> findPopularTags(Pageable pageable);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Tag> searchByName(@Param("query") String query, Pageable pageable);
}
