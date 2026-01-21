package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 채팅방 엔티티
 */
@Entity
@Getter
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}