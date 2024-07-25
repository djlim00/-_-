package com.kuit3.rematicserver.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostUpdateFormDto {
    private String title;
    private String content;
    private List<PostImageDto> images;
}
