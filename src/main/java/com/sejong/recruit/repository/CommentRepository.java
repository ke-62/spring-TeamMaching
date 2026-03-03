package com.sejong.recruit.repository;

import com.sejong.recruit.domain.project.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.project.id = :projectId ORDER BY c.createdAt ASC")
    List<Comment> findByProjectIdOrderByCreatedAtAsc(@Param("projectId") Long projectId);
}
