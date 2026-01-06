package com.sejong.recruit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String studentId;  // 학번
    
    @Column(nullable = false)
    private String name;  // 이름
    
    @Column(nullable = false)
    private String department;  // 학과
    
    @Column(nullable = false)
    private Integer grade;  // 학년
    
    @Column(unique = true)
    private String email;  // 이메일
    
    private String phoneNumber;  // 전화번호
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_tech_stacks", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tech_stack")
    @Builder.Default
    private List<String> techStacks = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    @Builder.Default
    private List<String> interests = new ArrayList<>();
    
    private String githubUrl;  // GitHub URL
    
    private String profileImage;  // 프로필 이미지 URL
    
    @Column(length = 1000)
    private String bio;  // 자기소개
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // 연관관계
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecruitPost> recruitPosts = new ArrayList<>();
    
    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}
