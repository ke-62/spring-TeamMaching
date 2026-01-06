package com.sejong.recruit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruit_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;  // 모집 제목
    
    @Column(nullable = false, length = 2000)
    private String description;  // 모집 설명
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectType projectType;  // 프로젝트 유형
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recruit_tech_stacks", joinColumns = @JoinColumn(name = "recruit_post_id"))
    @Column(name = "tech_stack")
    @Builder.Default
    private List<String> requiredTechStacks = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer recruitNumber;  // 모집 인원
    
    @Column(nullable = false)
    private LocalDateTime deadline;  // 모집 마감일
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isClosed = false;  // 모집 마감 여부
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;  // 작성자
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // 연관관계
    @OneToMany(mappedBy = "recruitPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
    
    public enum ProjectType {
        CREATIVE,    // 창의학기제
        CAPSTONE,    // 캡스톤디자인
        HACKATHON,   // 해커톤
        OTHER        // 기타
    }
}
