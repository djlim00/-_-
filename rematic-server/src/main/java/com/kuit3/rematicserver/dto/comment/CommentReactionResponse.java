package com.kuit3.rematicserver.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentReactionResponse {
    private Integer likeCount;
    private Integer hateCount;
    private Boolean isLiked;
    private Boolean isHated;
}