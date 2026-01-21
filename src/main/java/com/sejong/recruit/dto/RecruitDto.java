package com.sejong.recruit.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RecruitDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "제목을 입력해주세요")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
        private String title;
        
        @NotBlank(message = "설명을 입력해주세요")
        @Size(max = 2000, message = "설명은 2000자 이하로 입력해주세요")
        private String description;
        
        @NotNull(message = "프로젝트 타입을 선택해주세요")
        private String projectType;
        
        @NotEmpty(message = "필요한 기술 스택을 입력해주세요")
        private List<String> requiredTechStacks;
        
        @NotNull(message = "모집 인원을 입력해주세요")
        @Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다")
        @Max(value = 20, message = "모집 인원은 최대 20명까지 가능합니다")
        private Integer recruitNumber;
        
        @NotNull(message = "마감일을 입력해주세요")
        @Future(message = "마감일은 미래 날짜여야 합니다")
        private LocalDateTime deadline;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String projectType;
        private List<String> requiredTechStacks;
        private Integer recruitNumber;
        private LocalDateTime deadline;
        private Boolean isClosed;
        private UserDto author;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(RecruitPost post) {
            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .description(post.getDescription())
                    .projectType(post.getProjectType().name().toLowerCase())
                    .requiredTechStacks(post.getRequiredTechStacks())
                    .recruitNumber(post.getRecruitNumber())
                    .deadline(post.getDeadline())
                    .isClosed(post.getIsClosed())
                    .author(UserDto.from(post.getAuthor()))
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String description;
        private List<String> requiredTechStacks;
        private Integer recruitNumber;
        private LocalDateTime deadline;
        private Boolean isClosed;
    }
}
