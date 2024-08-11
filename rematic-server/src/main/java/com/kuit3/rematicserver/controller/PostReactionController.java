package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.service.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostReactionController {
    private final PostReactionService postReactionService;

    @PostMapping("/{post_id}/like")
    public BaseResponse<String> likePost(@PathVariable("post_id") Long postId, @PreAuthorizedUser long userId) {
        postReactionService.likePost(postId, userId);
        return new BaseResponse<>("좋아요");
    }

    @PostMapping("/{post_id}/hate")
    public BaseResponse<String> hatePost(@PathVariable("post_id") Long postId, @PreAuthorizedUser long userId) {
        postReactionService.hatePost(postId, userId);
        return new BaseResponse<>("싫어요");
    }
}
