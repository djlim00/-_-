package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.GetSearchResultResponse;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @GetMapping("search")
    public BaseResponse<GetSearchResultResponse> searchPosts(@PreAuthorizedUser long userId, @RequestParam String keyword, @RequestParam String category, @RequestParam Long lastId){
        log.info("PostController::search()");
        return new BaseResponse<>(postService.searchPageByKeywordAndCategory(userId, keyword, category, lastId));
    }
}
