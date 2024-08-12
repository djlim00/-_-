package com.kuit3.rematicserver.dto.post.commentresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
@ToString
public class CommentInfo {

    private Long commentId;
    private String writer;
    private Long writerId;
    private String imageUrl;
    private String comment;
    private Long parentId;
    private Timestamp time;
    private Long likes;
    private Boolean isLiked;
    private Long hates;
    private Boolean isHated;
    private Boolean isParentComment;

}
