package com.kuit3.rematicserver.dto.post;

import com.kuit3.rematicserver.entity.PostImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageDto {
    private Long post_image_id;
    private String image_url;
    private String image_description;

    public static PostImageDto entityToDto(PostImage postImage){
        return PostImageDto.builder()
                .post_image_id(postImage.getPostImageId())
                .image_url(postImage.getImageUrl())
                .image_description(postImage.getDescription())
                .build();
    }

}