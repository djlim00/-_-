package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import com.kuit3.rematicserver.dto.post.GetScrolledCommentsResponse;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public BaseResponse<GetClickedPostResponse> showClickedPost(@PreAuthorizedUser long userId, @PathVariable long postId) {
        log.info("PostController.showClickedPost");
        return new BaseResponse<>(postService.getValidatedClickedPostInfo(userId, postId));
    }
    @GetMapping("/guest/{postId}")
    public BaseResponse<GetClickedPostResponse> showClickedPostByGuestMode(@PathVariable long postId) {
        log.info("PostController.showClickedPostByGuestMode");
        return new BaseResponse<>(postService.getClickedPostInfo(postId));
    }

    @GetMapping("/comments/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostComments(@PathVariable long postId, @RequestParam long userId, @RequestParam String orderBy) {
        log.info("PostController.showPostComments");
        return new BaseResponse<>(postService.getValidatedCommentsByPostId(postId, userId, orderBy));
    }

    @GetMapping("/comments/guest/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostCommentsByGuestMode(@PathVariable long postId, @RequestParam String orderBy) {
        log.info("PostController.showPostComments");
        return new BaseResponse<>(postService.getCommentsByPostId(postId, orderBy));
    }


}
