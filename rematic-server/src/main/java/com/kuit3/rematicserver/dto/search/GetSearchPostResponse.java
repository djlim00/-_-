package com.kuit3.rematicserver.dto.search;

import com.kuit3.rematicserver.dto.post.GetSearchPostDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchPostResponse {
    private List<GetSearchPostDto> data;
    private Boolean has_next;
}
