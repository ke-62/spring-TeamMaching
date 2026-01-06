package com.sejong.recruit.dto;

import com.sejong.recruit.entity.Application;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ApplicationDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "지원 동기를 입력해주세요")
        @Size(max = 1000, message = "지원 동기는 1000자 이하로 입력해주세요")
        private String motivation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long recruitPostId;
        private UserDto applicant;
        private String motivation;
        private String status;
        private LocalDateTime createdAt;
        
        public static Response from(Application application) {
            return Response.builder()
                    .id(application.getId())
                    .recruitPostId(application.getRecruitPost().getId())
                    .applicant(UserDto.from(application.getApplicant()))
                    .motivation(application.getMotivation())
                    .status(application.getStatus().name().toLowerCase())
                    .createdAt(application.getCreatedAt())
                    .build();
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotBlank(message = "상태를 입력해주세요")
        private String status;  // accepted, rejected
    }
}
