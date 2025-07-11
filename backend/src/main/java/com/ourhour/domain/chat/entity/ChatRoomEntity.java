package com.ourhour.domain.chat.entity;

import com.ourhour.global.common.enums.TagColor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_chat_room")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String name;

    @Enumerated(EnumType.STRING)
    private TagColor color;

    private LocalDateTime createdAt;

    public void update(String name, TagColor color) {
        this.name = name;
        this.color = color;
    }
}
