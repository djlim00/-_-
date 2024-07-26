package com.kuit3.rematicserver.entity;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bulletin {
    private Long bulletinId;
    private String name;
    private String genre;
    private String originCategory;
    private String category;
    private String thumbnailImageUrl;
    private String PreviewVideoUrl;
    private String status;
}
