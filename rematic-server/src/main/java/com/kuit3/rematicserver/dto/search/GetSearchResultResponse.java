package com.kuit3.rematicserver.dto.search;

import com.kuit3.rematicserver.dto.post.GetPostDto;
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
