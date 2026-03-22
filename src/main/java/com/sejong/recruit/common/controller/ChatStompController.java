package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.ChatDto;
import com.sejong.recruit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatDto.SendMessageRequest request,
            Principal principal) {

        String studentId = principal.getName();
        ChatDto.MessageResponse response = chatService.sendMessage(studentId, roomId, request.getContent());

        // 해당 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/room/" + roomId, response);
    }
}
