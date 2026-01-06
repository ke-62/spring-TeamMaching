package com.sejong.recruit.repository;

import com.sejong.recruit.entity.RecruitPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {
    
    // 프로젝트 타입으로 검색
    Page<RecruitPost> findByProjectType(RecruitPost.ProjectType projectType, Pageable pageable);
    
    // 기술 스택 포함 검색
    @Query("SELECT r FROM RecruitPost r WHERE :techStack MEMBER OF r.requiredTechStacks")
    Page<RecruitPost> findByTechStack(@Param("techStack") String techStack, Pageable pageable);
    
    // 작성자로 검색
    List<RecruitPost> findByAuthorId(Long authorId);
    
    // 마감되지 않은 공고 조회
    Page<RecruitPost> findByIsClosedFalseAndDeadlineAfter(LocalDateTime now, Pageable pageable);
    
    // 프로젝트 타입 + 기술 스택 검색
    @Query("SELECT r FROM RecruitPost r WHERE r.projectType = :projectType AND :techStack MEMBER OF r.requiredTechStacks")
    Page<RecruitPost> findByProjectTypeAndTechStack(
            @Param("projectType") RecruitPost.ProjectType projectType,
            @Param("techStack") String techStack,
            Pageable pageable
    );
}
