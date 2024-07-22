package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.search.GetSearchPostResponse;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import com.kuit3.rematicserver.dto.search.UserRecommendableKeywordsResponse;
import com.kuit3.rematicserver.service.PostService;
import com.kuit3.rematicserver.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final PostService postService;
    @GetMapping("post")
    public BaseResponse<GetSearchPostResponse> search(@PreAuthorizedUser long userId,
                                                      @RequestParam String keyword,
                                                      @RequestParam String category,
                                                      @RequestParam(required = false) Long lastId){
        log.info("PostController::search()");
        return new BaseResponse<>(postService.searchPage(userId, keyword, category, lastId));
    }

    @GetMapping("post/guest")
    public BaseResponse<GetSearchPostResponse> search_guestmode(@RequestParam String keyword,
                                                                @RequestParam String category,
                                                                @RequestParam(required = false) Long lastId){
        log.info("PostController::search_guestmode()");
        return new BaseResponse<>(postService.searchPage(null, keyword, category, lastId));
    }

    @GetMapping("/recentkeyword")
    public BaseResponse<List<UserRecentKeywordResponse>> getUserRecentKeywords(@PreAuthorizedUser long userId) {
//    @GetMapping("/recentkeyword/{userId}")
//    public BaseResponse<List<UserRecentKeywordResponse>> getUserRecentKeywords(@PathVariable long userId) {
        log.info("SearchController.getUserRecentKeywords");
        return new BaseResponse<>(searchService.getUserRecentKeywords(userId));
    }

    @GetMapping("/recommendation")
    public BaseResponse<List<UserRecommendableKeywordsResponse>> getUserRecommendableKeywords(@PreAuthorizedUser long userId) {
//    @GetMapping("/recommendation/{userId}")
//    public BaseResponse<List<UserRecommendableKeywordsResponse>> getUserRecommendableKeywords(@PathVariable long userId) {
        log.info("SearchController.getUserRecommendableKeywords");
        return new BaseResponse<>(searchService.getUserRecommendableKeywords(userId));
    }

    @PostMapping("/keyword/{keyword_id}/delete")
    public BaseResponse<String> deactivateUserKeyword(@PreAuthorizedUser long userId, @PathVariable long keyword_id) {
//    @PostMapping("/keyword/{userId}/{keyword_id}/delete")
//    public BaseResponse<String> deactivateUserKeyword(@PathVariable long userId, @PathVariable long keyword_id) {
        log.info("SearchController.deactivateUserKeyword");
        return new BaseResponse<>(searchService.deactivateUserKeyword(userId, keyword_id));
    }
}
