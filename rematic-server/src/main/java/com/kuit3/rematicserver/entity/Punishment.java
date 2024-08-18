package com.kuit3.rematicserver.entity;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Punishment {
    private Long punishmentId;
    private LocalDateTime createdAt;
    private String reason;
    private Long bulletinId;
    private String content;
    private LocalDateTime endAt;
    private Long userId;

}
