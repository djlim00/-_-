package com.kuit3.rematicserver.dto.post.commentresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyComment {

    private CommentInfo parentComment;
    private List<CommentInfo> childComments;

}
