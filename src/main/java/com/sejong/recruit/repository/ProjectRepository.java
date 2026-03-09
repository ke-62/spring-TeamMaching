package com.sejong.recruit.repository;

import com.sejong.recruit.domain.project.entity.Project;
import com.sejong.recruit.domain.project.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    List<Project> findByLeaderId(Long leaderId);

    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.deadline > :now")
    Page<Project> findActiveByStatus(@Param("status") ProjectStatus status, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.requiredRoles LIKE %:techStack%")
    Page<Project> findByTechStack(@Param("techStack") String techStack, Pageable pageable);

    Page<Project> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
