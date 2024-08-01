package com.kuit3.rematicserver.dto.post;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {
    private String title;
    private String content;
    private Boolean has_image;
    private String category;
    private String genre;
    private Boolean anonymity;
    private Long user_id;
    private Long bulletin_id;
}
