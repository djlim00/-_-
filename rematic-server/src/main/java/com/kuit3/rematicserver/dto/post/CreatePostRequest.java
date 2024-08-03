package com.kuit3.rematicserver.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    @JsonProperty("title")
    private String title;
    @JsonProperty("content")
    private String content;
    @JsonProperty("has_image")
    private Boolean has_image;
    private String category;
    private String genre;
    @JsonProperty("anonymity")
    private Boolean anonymity;
    private Long user_id;
    @JsonProperty("bulletin_id")
    private Long bulletin_id;
}
