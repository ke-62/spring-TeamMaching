package com.sejong.recruit.service;

import com.sejong.recruit.dto.RecruitDto;
import com.sejong.recruit.entity.RecruitPost;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.exception.BusinessException;
import com.sejong.recruit.repository.RecruitPostRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitService {
    
    private final RecruitPostRepository recruitPostRepository;
    private final UserRepository userRepository;
    
    /**
     * 모집 공고 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<RecruitDto.Response> getRecruitPosts(String projectType, String techStack, Pageable pageable) {
        Page<RecruitPost> posts;
        
        if (projectType != null && techStack != null) {
            // 프로젝트 타입 + 기술 스택 필터링
            RecruitPost.ProjectType type = parseProjectType(projectType);
            posts = recruitPostRepository.findByProjectTypeAndTechStack(type, techStack, pageable);
        } else if (projectType != null) {
            // 프로젝트 타입만 필터링
            RecruitPost.ProjectType type = parseProjectType(projectType);
            posts = recruitPostRepository.findByProjectType(type, pageable);
        } else if (techStack != null) {
            // 기술 스택만 필터링
            posts = recruitPostRepository.findByTechStack(techStack, pageable);
        } else {
            // 전체 조회
            posts = recruitPostRepository.findAll(pageable);
        }
        
        return posts.map(RecruitDto.Response::from);
    }
    
    /**
     * 모집 공고 상세 조회
     */
    @Transactional(readOnly = true)
    public RecruitDto.Response getRecruitPost(Long id) {
        RecruitPost post = recruitPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("모집 공고를 찾을 수 없습니다."));
        
        return RecruitDto.Response.from(post);
    }
    
    /**
     * 모집 공고 생성
     */
    @Transactional
    public RecruitDto.Response createRecruitPost(String studentId, RecruitDto.CreateRequest request) {
        User author = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        
        RecruitPost post = RecruitPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .projectType(parseProjectType(request.getProjectType()))
                .requiredTechStacks(request.getRequiredTechStacks())
                .recruitNumber(request.getRecruitNumber())
                .deadline(request.getDeadline())
                .author(author)
                .isClosed(false)
                .build();
        
        RecruitPost savedPost = recruitPostRepository.save(post);
        log.info("모집 공고 생성: {} by {}", savedPost.getId(), studentId);
        
        return RecruitDto.Response.from(savedPost);
    }
    
    /**
     * 모집 공고 수정
     */
    @Transactional
    public RecruitDto.Response updateRecruitPost(Long id, String studentId, RecruitDto.UpdateRequest request) {
        RecruitPost post = recruitPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("모집 공고를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!post.getAuthor().getStudentId().equals(studentId)) {
            throw new BusinessException("수정 권한이 없습니다.");
        }
        
        // 수정
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }
        if (request.getRequiredTechStacks() != null) {
            post.setRequiredTechStacks(request.getRequiredTechStacks());
        }
        if (request.getRecruitNumber() != null) {
            post.setRecruitNumber(request.getRecruitNumber());
        }
        if (request.getDeadline() != null) {
            post.setDeadline(request.getDeadline());
        }
        if (request.getIsClosed() != null) {
            post.setIsClosed(request.getIsClosed());
        }
        
        RecruitPost updatedPost = recruitPostRepository.save(post);
        log.info("모집 공고 수정: {}", id);
        
        return RecruitDto.Response.from(updatedPost);
    }
    
    /**
     * 모집 공고 삭제
     */
    @Transactional
    public void deleteRecruitPost(Long id, String studentId) {
        RecruitPost post = recruitPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("모집 공고를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!post.getAuthor().getStudentId().equals(studentId)) {
            throw new BusinessException("삭제 권한이 없습니다.");
        }
        
        recruitPostRepository.delete(post);
        log.info("모집 공고 삭제: {}", id);
    }
    
    /**
     * 프로젝트 타입 문자열 파싱
     */
    private RecruitPost.ProjectType parseProjectType(String type) {
        try {
            return RecruitPost.ProjectType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("유효하지 않은 프로젝트 타입입니다: " + type);
        }
    }
}
