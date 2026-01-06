package com.sejong.recruit.repository;

import com.sejong.recruit.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    // 모집 공고의 지원서 목록
    List<Application> findByRecruitPostId(Long recruitPostId);
    
    // 사용자의 지원 목록
    List<Application> findByApplicantId(Long applicantId);
    
    // 특정 모집 공고에 대한 사용자의 지원 여부 확인
    Optional<Application> findByRecruitPostIdAndApplicantId(Long recruitPostId, Long applicantId);
    
    // 상태별 지원서 조회
    List<Application> findByApplicantIdAndStatus(Long applicantId, Application.ApplicationStatus status);
}
