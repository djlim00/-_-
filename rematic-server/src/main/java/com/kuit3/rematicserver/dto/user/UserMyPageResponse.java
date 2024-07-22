package com.kuit3.rematicserver.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMyPageResponse {
    private String nickName;
    private String imageUrl;
    private String introduction;
    private Long myPosts;
    private Long myComments;
    private Long myScraps;
}
