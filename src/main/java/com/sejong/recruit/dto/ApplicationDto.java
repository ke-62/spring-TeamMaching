package com.sejong.recruit.dto;

import com.sejong.recruit.domain.recruitment.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApplicationDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String motivation;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        private String status; // "accepted" or "rejected"
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long recruitPostId;
        private Long applicantId;
        private UserDto applicant;
        private String motivation;
        private String status;
        private String createdAt;

        public static Response from(Application application) {
            return Response.builder()
                    .id(application.getId())
                    .recruitPostId(application.getProject().getId())
                    .applicantId(application.getApplicant().getId())
                    .applicant(UserDto.from(application.getApplicant()))
                    .motivation(application.getMessage())
                    .status(application.getStatus().name().toLowerCase())
                    .createdAt(application.getCreatedAt() != null ? application.getCreatedAt().toString() : null)
                    .build();
        }
    }
}
