package com.kuit3.rematicserver.dto.search;

import com.kuit3.rematicserver.domain.Work;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendableKeywordsResponse {

    private String imageUrl;
    private List<Work> works;

}