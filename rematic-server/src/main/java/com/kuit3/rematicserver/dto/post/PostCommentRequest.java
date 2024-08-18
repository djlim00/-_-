package com.kuit3.rematicserver.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentRequest {

    private String sentences;
    private Long parentCommentId;
}
