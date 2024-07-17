package com.kuit3.rematicserver.dto.post;

import com.kuit3.rematicserver.dto.post.postresponse.ImageInfo;
import com.kuit3.rematicserver.dto.post.postresponse.PostInfo;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetClickedPostResponse {

    private String bulletinName;
    private PostInfo postInfo;
    private UserInfo userInfo;
    private List<ImageInfo> imageInfo;

}
