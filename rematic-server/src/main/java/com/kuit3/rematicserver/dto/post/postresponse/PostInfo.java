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
    private Long hates;
    private Long scraps;
}
