package com.cesar.Chat.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@Builder
@Table("conversations")
public class Conversation implements Serializable {
    @PrimaryKeyColumn(ordinal = 0)
    private UUID id;
    @PrimaryKeyColumn(name = "participant_ids", ordinal = 1)
    private List<Long> participantIds;
    @Column("recreate_for")
    private List<Long> recreateFor;
    @Column("created_at")
    private LocalDateTime createdAt;
}