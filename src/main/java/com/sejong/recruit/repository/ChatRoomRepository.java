package com.sejong.recruit.repository;

import com.sejong.recruit.domain.chat.entity.ChatRoom;
import com.sejong.recruit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT r FROM ChatRoom r WHERE " +
           "(r.user1 = :a AND r.user2 = :b) OR (r.user1 = :b AND r.user2 = :a)")
    Optional<ChatRoom> findDmRoom(@Param("a") User a, @Param("b") User b);

    @Query("SELECT r FROM ChatRoom r WHERE r.user1 = :user OR r.user2 = :user ORDER BY r.updatedAt DESC")
    List<ChatRoom> findAllByUser(@Param("user") User user);
}
