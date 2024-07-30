package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{comment_id}/like")
    public BaseResponse<String> likeComment(@PathVariable("comment_id") Long commentId, @PreAuthorizedUser Long userId){ //  @RequestParam("user_id") Long userId
        commentService.likeComment(commentId, userId);
        return new BaseResponse<>("댓글 좋아요");
    }

    @PostMapping("/{comment_id}/hate")
    public BaseResponse<String> hateComment(@PathVariable("comment_id") Long commentId,  @PreAuthorizedUser Long userId){
        commentService.hateComment(commentId, userId);
        return new BaseResponse<>("댓글 싫어요");
    }

    @PostMapping("/comment/{comment_id}")
    public BaseResponse<String> dormantUserComment(@PreAuthorizedUser long userId,
                                                   @PathVariable("comment_id") long commentId) {
        log.info("PostController.dormantUserComment");
        return new BaseResponse<>(commentService.dormantUserComment(userId, commentId));
    }
}
