package com.kuit3.rematicserver.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImage {
    private Long postImageId;
    private String imageUrl;
    private String description;
    private String status;
    private Long postId;
    private Long order;
}
