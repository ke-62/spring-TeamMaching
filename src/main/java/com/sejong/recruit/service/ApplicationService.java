package com.sejong.recruit.service;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.entity.Application;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.global.exception.BusinessException;
import com.sejong.recruit.repository.ApplicationRepository;
import com.sejong.recruit.repository.RecruitPostRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final RecruitPostRepository recruitPostRepository;
    private final UserRepository userRepository;
    
    /**
     * 지원하기
     */
    @Transactional
    public ApplicationDto.Response applyToRecruit(Long recruitPostId, String studentId, ApplicationDto.CreateRequest request) {
        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new BusinessException("모집 공고를 찾을 수 없습니다."));
        
        User applicant = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        
        // 본인 공고에는 지원 불가
        if (recruitPost.getAuthor().getId().equals(applicant.getId())) {
            throw new BusinessException("본인이 작성한 공고에는 지원할 수 없습니다.");
        }
        
        // 마감 확인
        if (recruitPost.getIsClosed() || recruitPost.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException("마감된 공고입니다.");
        }
        
        // 중복 지원 확인
        if (applicationRepository.findByRecruitPostIdAndApplicantId(recruitPostId, applicant.getId()).isPresent()) {
            throw new BusinessException("이미 지원한 공고입니다.");
        }
        
        Application application = Application.builder()
                .recruitPost(recruitPost)
                .applicant(applicant)
                .motivation(request.getMotivation())
                .status(Application.ApplicationStatus.PENDING)
                .build();
        
        Application savedApplication = applicationRepository.save(application);
        log.info("지원 완료: 공고 {} - 지원자 {}", recruitPostId, studentId);
        
        return ApplicationDto.Response.from(savedApplication);
    }
    
    /**
     * 지원 취소
     */
    @Transactional
    public void cancelApplication(Long applicationId, String studentId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("지원서를 찾을 수 없습니다."));
        
        // 본인 지원서 확인
        if (!application.getApplicant().getStudentId().equals(studentId)) {
            throw new BusinessException("취소 권한이 없습니다.");
        }
        
        applicationRepository.delete(application);
        log.info("지원 취소: {}", applicationId);
    }
    
    /**
     * 모집 공고의 지원자 목록 조회 (공고 작성자용)
     */
    @Transactional(readOnly = true)
    public List<ApplicationDto.Response> getApplicationsByRecruitPost(Long recruitPostId, String studentId) {
        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new BusinessException("모집 공고를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!recruitPost.getAuthor().getStudentId().equals(studentId)) {
            throw new BusinessException("조회 권한이 없습니다.");
        }
        
        List<Application> applications = applicationRepository.findByRecruitPostId(recruitPostId);
        
        return applications.stream()
                .map(ApplicationDto.Response::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 내 지원 내역 조회
     */
    @Transactional(readOnly = true)
    public List<ApplicationDto.Response> getMyApplications(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        
        List<Application> applications = applicationRepository.findByApplicantId(user.getId());
        
        return applications.stream()
                .map(ApplicationDto.Response::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 지원 상태 변경 (수락/거절)
     */
    @Transactional
    public ApplicationDto.Response updateApplicationStatus(Long recruitPostId, Long applicationId, 
                                                           String studentId, ApplicationDto.UpdateStatusRequest request) {
        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new BusinessException("모집 공고를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!recruitPost.getAuthor().getStudentId().equals(studentId)) {
            throw new BusinessException("권한이 없습니다.");
        }
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("지원서를 찾을 수 없습니다."));
        
        // 해당 공고의 지원서인지 확인
        if (!application.getRecruitPost().getId().equals(recruitPostId)) {
            throw new BusinessException("잘못된 요청입니다.");
        }
        
        // 상태 변경
        try {
            Application.ApplicationStatus status = Application.ApplicationStatus.valueOf(request.getStatus().toUpperCase());
            application.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("유효하지 않은 상태입니다: " + request.getStatus());
        }
        
        Application updatedApplication = applicationRepository.save(application);
        log.info("지원 상태 변경: {} - {}", applicationId, request.getStatus());
        
        return ApplicationDto.Response.from(updatedApplication);
    }
}
