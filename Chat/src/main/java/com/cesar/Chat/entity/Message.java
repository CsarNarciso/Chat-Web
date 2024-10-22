package com.cesar.Chat.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("messages")
public class Message implements Serializable {
    @PrimaryKeyColumn(name = "id", ordinal = 0)
    private UUID id;
    @PrimaryKeyColumn(name = "conversation_id", ordinal = 1)
    private UUID conversationId;
    @PrimaryKeyColumn(name = "sender_id", ordinal = 2)
    private Long senderId;
    private String content;
    @Column("sent_at")
    private LocalDateTime sentAt;
    private boolean read;
}