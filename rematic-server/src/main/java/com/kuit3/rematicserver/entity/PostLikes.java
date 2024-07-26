package com.kuit3.rematicserver.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikes {
    private Long postLikesId;
    private LocalDateTime createdAt;
    private Long userId;
    private Long postId;
}
