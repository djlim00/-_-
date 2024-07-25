package com.kuit3.rematicserver.dto.post.postresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostInfo {

    private String title;
    private String content;
    private Long likes;
    private Boolean isLiked;
    private Long hates;
    private Boolean isHated;
    private Long scraps;
    private Boolean isScraped;
}
