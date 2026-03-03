package com.sejong.recruit.service;

import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto getMyProfile(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserDto.from(user);
    }

    @Transactional
    public UserDto updateMyProfile(String studentId, String name, List<String> techStacks,
                                   List<String> interests, String githubUrl, String bio) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (name != null) user.setFullName(name);
        if (bio != null) user.setBio(bio);
        if (githubUrl != null) user.setGithubUrl(githubUrl);
        if (techStacks != null) user.setTechStack(String.join(",", techStacks));
        if (interests != null) user.setCollaborationKeywords(String.join(",", interests));

        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::from)
                .toList();
    }
}
