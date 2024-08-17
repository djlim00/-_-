package com.kuit3.rematicserver.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private Long commentId;
    private String sentences;
    private Long likes;
    private Long hates;
    private String commentImageUrl;
    private Long parentId;
    private String status;
    private LocalDateTime createdAt;
    private String anonymity;
    private Long postId;
    private Long userId;
}
