package com.kuit3.rematicserver.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long postId;
    private String title;
    private String content;
    private Boolean hasImage;
    private String category;
    private Long hates;

    private Long likes;
    private Long scraps;
    private Long views;
    private Long realtimeViews;
    private Boolean anonymity;

    private String status;

    private Long userId;
    private Long bulletinId;
}
