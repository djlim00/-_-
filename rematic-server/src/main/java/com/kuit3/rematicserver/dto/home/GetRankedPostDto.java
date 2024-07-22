package com.kuit3.rematicserver.dto.home;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRankedPostDto {
    private int rank;
    private Long post_id;
    private String title;
    private String content;
    private String bulletin;
    private Long likes;
    private Long hates;
    private Long views;
    private Long scraps;
    private String image_url;
}
