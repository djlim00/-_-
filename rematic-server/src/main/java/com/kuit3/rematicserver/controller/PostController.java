package com.kuit3.rematicserver.controller;

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

    //모든 사람에게 글을 보는건 허용되지만, 댓글을 달 때는 preauthorize가 있어야 한다.
    @GetMapping("/{postId}")
    public BaseResponse<GetClickedPostResponse> showClickedPost(@PathVariable long postId) {
        log.info("PostController.showClickedPost");
        return new BaseResponse<>(postService.getClickedPostInfo(postId));
    }

    @GetMapping("/comments/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostComments(@PathVariable long postId, @RequestParam long lastId, @RequestParam String orderBy) {
        log.info("PostController.showPostComments");
        return new BaseResponse<>(postService.getCommentsByPostId(postId, lastId, orderBy));
    }


}
