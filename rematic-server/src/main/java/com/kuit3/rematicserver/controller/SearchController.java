package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import com.kuit3.rematicserver.dto.search.UserRecommendableKeywordsResponse;
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
