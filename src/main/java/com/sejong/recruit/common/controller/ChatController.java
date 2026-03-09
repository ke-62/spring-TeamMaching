package com.sejong.recruit.common.controller;

import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.dto.ChatDto;
import com.sejong.recruit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private String requireAuth(UserDetails userDetails) {
        if (userDetails == null) throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        return userDetails.getUsername();
    }

    @GetMapping("/rooms")
    public List<ChatDto.RoomResponse> getRooms(@AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getRooms(requireAuth(userDetails));
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatDto.RoomResponse createRoom(
            @RequestBody ChatDto.CreateRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getOrCreateRoom(requireAuth(userDetails), request.getPartnerId());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatDto.MessageResponse> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getMessages(requireAuth(userDetails), roomId);
    }

    @PostMapping("/rooms/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatDto.MessageResponse sendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatDto.SendMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return chatService.sendMessage(requireAuth(userDetails), roomId, request.getContent());
    }
}
