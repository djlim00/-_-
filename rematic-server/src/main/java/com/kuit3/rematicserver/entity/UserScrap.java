package com.kuit3.rematicserver.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserScrap {
    private Long scrapId;
    private Long userId;
    private Long postId;
}
