package com.sejong.recruit.dto;

import com.sejong.recruit.domain.chat.entity.ChatMessage;
import com.sejong.recruit.domain.chat.entity.ChatRoom;
import com.sejong.recruit.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.format.DateTimeFormatter;

public class ChatDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRoomRequest {
        private Long partnerId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest {
        private String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomResponse {
        private Long roomId;
        private Long partnerId;
        private String partnerName;
        private String partnerDepartment;
        private String lastMessage;
        private String lastAt;

        public static RoomResponse from(ChatRoom room, User me, String lastMessage) {
            User partner = room.getUser1().getId().equals(me.getId())
                    ? room.getUser2() : room.getUser1();
            return RoomResponse.builder()
                    .roomId(room.getId())
                    .partnerId(partner.getId())
                    .partnerName(partner.getFullName())
                    .partnerDepartment(partner.getMajor())
                    .lastMessage(lastMessage)
                    .lastAt(room.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MessageResponse {
        private Long id;
        private Long senderId;
        private String senderName;
        private String content;
        private Boolean isRead;
        private String createdAt;

        public static MessageResponse from(ChatMessage msg) {
            return MessageResponse.builder()
                    .id(msg.getId())
                    .senderId(msg.getSender().getId())
                    .senderName(msg.getSender().getFullName())
                    .content(msg.getContent())
                    .isRead(msg.getIsRead())
                    .createdAt(msg.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        }
    }
}
