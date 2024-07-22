package com.kuit3.rematicserver.dto.post;

import com.kuit3.rematicserver.dto.post.commentresponse.FamilyComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetScrolledCommentsResponse {

    private List<FamilyComment> commentList;
    private Long countOfComments;
    private Long lastId;
}
