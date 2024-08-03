package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.bulletin.BulletinDto;
import com.kuit3.rematicserver.dto.search.SearchPostResponse;
import com.kuit3.rematicserver.service.BulletinService;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("bulletin")
@RequiredArgsConstructor
public class BulletinController {
    private final BulletinService bulletinService;
    private final PostService postService;

    @GetMapping
    public BaseResponse<List<BulletinDto>> getBulletinsByCategory(@RequestParam String category) {
        List<BulletinDto> bulletins = bulletinService.getBulletinsByCategory(category);
        return new BaseResponse<>(bulletins);
    }

    @GetMapping("{bulletin_id}/search")
    public BaseResponse<SearchPostResponse> getBulletinPostsByKeyword(@PreAuthorizedUser long userId,
                                                                      @PathVariable("bulletin_id") Long bulletinId,
                                                                      @RequestParam String keyword,
                                                                      @RequestParam(required = false) Long lastId){
        log.info("BulletinController::getBulletinPostsByKeyword()");
        return new BaseResponse<>(postService.searchBulletinPage(userId, bulletinId, keyword, lastId));
    }

    @GetMapping("{bulletin_id}/search/guest")
    public BaseResponse<SearchPostResponse> getBulletinPostsByKeyword_guestmode(@PathVariable("bulletin_id") Long bulletinId,
                                                                      @RequestParam String keyword,
                                                                      @RequestParam(required = false) Long lastId){
        log.info("BulletinController::getBulletinPostsByKeyword_guestmode()");
        return new BaseResponse<>(postService.searchBulletinPage_guestmode(bulletinId, keyword, lastId));
    }

    @GetMapping("{bulletin_id}/posts")
    public BaseResponse<SearchPostResponse> getBulletinPosts(@PathVariable("bulletin_id") Long bulletinId,
                                                             @RequestParam(required = false) Long lastId){
        log.info("BulletinController::getBulletinPosts()");
        return new BaseResponse<>(postService.searchBulletinPage_guestmode(bulletinId, "", lastId));
    }

}
