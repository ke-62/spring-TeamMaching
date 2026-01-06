package com.sejong.recruit.service;

import com.sejong.recruit.dto.AuthDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.exception.BusinessException;
import com.sejong.recruit.repository.UserRepository;
import com.sejong.recruit.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final SejongPortalService sejongPortalService;
    private final JwtUtil jwtUtil;
    
    /**
     * 세종대학교 학사정보시스템을 통한 로그인
     */
    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        log.info("로그인 시도: {}", request.getStudentId());
        
        // 1. 세종대 포털 인증 및 학생 정보 조회
        SejongPortalService.StudentInfo studentInfo;
        try {
            studentInfo = sejongPortalService.authenticateAndGetStudentInfo(
                    request.getStudentId(),
                    request.getPassword()
            );
        } catch (Exception e) {
            log.error("세종대 포털 인증 실패: {}", e.getMessage());
            throw new BusinessException("학번 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 2. 사용자 조회 또는 생성
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseGet(() -> createUserFromStudentInfo(studentInfo));
        
        // 학생 정보 업데이트 (학년, 학과 등이 변경될 수 있음)
        updateUserInfo(user, studentInfo);
        
        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getStudentId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getStudentId());
        
        log.info("로그인 성공: {}", user.getStudentId());
        
        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserDto.from(user))
                .build();
    }
    
    /**
     * 학생 정보로부터 새 사용자 생성
     */
    private User createUserFromStudentInfo(SejongPortalService.StudentInfo info) {
        User newUser = User.builder()
                .studentId(info.getStudentId())
                .name(info.getName())
                .department(info.getDepartment())
                .grade(info.getGrade())
                .email(info.getEmail())
                .build();
        
        return userRepository.save(newUser);
    }
    
    /**
     * 사용자 정보 업데이트
     */
    private void updateUserInfo(User user, SejongPortalService.StudentInfo info) {
        boolean updated = false;
        
        if (!user.getName().equals(info.getName())) {
            user.setName(info.getName());
            updated = true;
        }
        
        if (!user.getDepartment().equals(info.getDepartment())) {
            user.setDepartment(info.getDepartment());
            updated = true;
        }
        
        if (!user.getGrade().equals(info.getGrade())) {
            user.setGrade(info.getGrade());
            updated = true;
        }
        
        if (updated) {
            userRepository.save(user);
            log.info("사용자 정보 업데이트: {}", user.getStudentId());
        }
    }
    
    /**
     * 토큰 갱신
     */
    public AuthDto.TokenResponse refreshToken(AuthDto.RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        String studentId = jwtUtil.getStudentIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(studentId);
        
        return AuthDto.TokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
    
    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        
        return UserDto.from(user);
    }
}
