package com.sejong.recruit.repository;

import com.sejong.recruit.domain.recruitment.entity.Application;
import com.sejong.recruit.domain.recruitment.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByProjectId(Long projectId);

    List<Application> findByApplicantId(Long applicantId);

    Optional<Application> findByProjectIdAndApplicantId(Long projectId, Long applicantId);

    List<Application> findByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);

    boolean existsByProjectIdAndApplicantId(Long projectId, Long applicantId);
}
