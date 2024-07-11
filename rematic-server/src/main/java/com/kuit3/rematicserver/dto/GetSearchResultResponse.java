package com.kuit3.rematicserver.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchResultResponse {
    private List<GetPostDto> data;
    private Boolean has_next;
}
