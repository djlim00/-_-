package com.kuit3.rematicserver.dto.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostReport {
    private Long reportId;
    private LocalDateTime createdAt;
    private String type;
    private Long reporterId;
    private Long reportedUserId;
    private Long postId;
}
