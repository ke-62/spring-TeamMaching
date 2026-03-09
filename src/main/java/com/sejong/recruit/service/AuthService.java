package com.sejong.recruit.service;

import com.sejong.recruit.dto.AuthDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.UserRepository;
import com.sejong.recruit.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SejongPortalService sejongPortalService;

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        Map<String, String> studentInfo = sejongPortalService.authenticateAndGetStudentInfo(
                request.getStudentId(), request.getPassword()
        );

        if (studentInfo == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        User user = userRepository.findByStudentId(request.getStudentId())
                .map(existingUser -> {
                    existingUser.setFullName(studentInfo.getOrDefault("name", existingUser.getFullName()));
                    existingUser.setMajor(studentInfo.getOrDefault("department", existingUser.getMajor()));
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> createUserFromStudentInfo(request.getStudentId(), studentInfo));

        String accessToken = jwtUtil.generateAccessToken(user.getStudentId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getStudentId());

        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserDto.from(user))
                .build();
    }

    public AuthDto.TokenResponse refreshToken(AuthDto.RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        String studentId = jwtUtil.getStudentIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(studentId);

        return AuthDto.TokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserDto.from(user);
    }

    private User createUserFromStudentInfo(String studentId, Map<String, String> info) {
        User user = User.builder()
                .email(studentId + "@sejong.ac.kr")
                .passwordHash("PORTAL_AUTH")
                .fullName(info.getOrDefault("name", "세종학생"))
                .studentId(studentId)
                .major(info.getOrDefault("department", ""))
                .build();
        return userRepository.save(user);
    }
}
