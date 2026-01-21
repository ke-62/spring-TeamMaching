package com.sejong.recruit.service;

import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.global.exception.BusinessException;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * 사용자 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        
        return UserDto.from(user);
    }
    
    /**
     * 내 프로필 수정
     */
    @Transactional
    public UserDto updateMyProfile(String studentId, UpdateProfileRequest request) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        
        // 수정 가능한 필드만 업데이트
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getTechStacks() != null) {
            user.setTechStacks(request.getTechStacks());
        }
        if (request.getInterests() != null) {
            user.setInterests(request.getInterests());
        }
        if (request.getGithubUrl() != null) {
            user.setGithubUrl(request.getGithubUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("프로필 업데이트: {}", studentId);
        
        return UserDto.from(updatedUser);
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UpdateProfileRequest {
        private String phoneNumber;
        private List<String> techStacks;
        private List<String> interests;
        private String githubUrl;
        private String bio;
    }
}
