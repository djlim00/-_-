package com.kuit3.rematicserver.dto.post;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSearchPostDto {
    private Long post_id;
//    private String status;
    private String title;
    private String content;
    private Long likes;
    private Long hates;
    private Long views;
    private Long scraps;
    private String image_url;
}
