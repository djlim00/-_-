package com.kuit3.rematicserver.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ranking {
    private Long rankingId;
    private Long recentLikes;
    private Long recentHates;
    private String category;
    private Long postId;
}
