package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.service.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostReactionController {
    private final PostReactionService postReactionService;

    @PostMapping("/{post_id}/like")
    public BaseResponse<Map<String, Object>> likePost(@PathVariable("post_id") Long postId, @PreAuthorizedUser long userId) {
        Map<String, Object> result = postReactionService.likePost(postId, userId);
        return new BaseResponse<>(result);
    }

    @PostMapping("/{post_id}/hate")
    public BaseResponse<Map<String, Object>> hatePost(@PathVariable("post_id") Long postId, @PreAuthorizedUser long userId) {
        Map<String, Object> result = postReactionService.hatePost(postId, userId);
        return new BaseResponse<>(result);
    }
}
