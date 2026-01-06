package com.sejong.recruit.security;

import com.sejong.recruit.entity.User;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + studentId));
        
        return new org.springframework.security.core.userdetails.User(
                user.getStudentId(),
                "",  // 비밀번호는 세종대 포털에서 관리하므로 빈 문자열
                new ArrayList<>()
        );
    }
}
