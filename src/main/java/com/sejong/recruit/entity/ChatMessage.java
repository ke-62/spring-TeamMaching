package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 채팅 메시지 엔티티
 */
@Entity
@Getter
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    private boolean isRead = false;

    public void markAsRead() {
        this.isRead = true;
    }
}