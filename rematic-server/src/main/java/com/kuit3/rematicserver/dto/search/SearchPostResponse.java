package com.kuit3.rematicserver.dto.search;

import com.kuit3.rematicserver.dto.post.SearchPostDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPostResponse {
    private List<SearchPostDto> data;
    private Boolean has_next;
}
