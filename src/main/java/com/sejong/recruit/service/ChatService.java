package com.sejong.recruit.service;

import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.domain.chat.entity.ChatMessage;
import com.sejong.recruit.domain.chat.entity.ChatRoom;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.dto.ChatDto;
import com.sejong.recruit.repository.ChatMessageRepository;
import com.sejong.recruit.repository.ChatRoomRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private User getUser(String studentId) {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ChatDto.RoomResponse> getRooms(String studentId) {
        User me = getUser(studentId);
        return chatRoomRepository.findAllByUser(me).stream()
                .map(room -> {
                    List<ChatMessage> msgs = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);
                    String last = msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1).getContent();
                    return ChatDto.RoomResponse.from(room, me, last);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatDto.RoomResponse getOrCreateRoom(String studentId, Long partnerId) {
        User me = getUser(studentId);
        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findDmRoom(me, partner)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder().user1(me).user2(partner).build()));

        List<ChatMessage> msgs = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);
        String last = msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1).getContent();
        return ChatDto.RoomResponse.from(room, me, last);
    }

    @Transactional(readOnly = true)
    public List<ChatDto.MessageResponse> getMessages(String studentId, Long roomId) {
        User me = getUser(studentId);
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.getUser1().getId().equals(me.getId()) && !room.getUser2().getId().equals(me.getId())) {
            throw new BusinessException(ErrorCode.CHAT_FORBIDDEN);
        }

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room).stream()
                .map(ChatDto.MessageResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatDto.MessageResponse sendMessage(String studentId, Long roomId, String content) {
        User me = getUser(studentId);
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.getUser1().getId().equals(me.getId()) && !room.getUser2().getId().equals(me.getId())) {
            throw new BusinessException(ErrorCode.CHAT_FORBIDDEN);
        }

        ChatMessage msg = ChatMessage.builder()
                .chatRoom(room)
                .sender(me)
                .content(content)
                .build();

        return ChatDto.MessageResponse.from(chatMessageRepository.save(msg));
    }
}
