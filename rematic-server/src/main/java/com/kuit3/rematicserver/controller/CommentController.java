package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.comment.CommentReactionResponse;
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
    public BaseResponse<CommentReactionResponse> likeComment(@PathVariable("comment_id") Long commentId, @PreAuthorizedUser long userId) {
        CommentReactionResponse response = commentService.likeComment(commentId, userId);
        return new BaseResponse<>(response);
    }

    @PostMapping("/{comment_id}/hate")
    public BaseResponse<CommentReactionResponse> hateComment(@PathVariable("comment_id") Long commentId, @PreAuthorizedUser long userId) {
        CommentReactionResponse response = commentService.hateComment(commentId, userId);
        return new BaseResponse<>(response);
    }


    @PostMapping("/block")
    public BaseResponse<String> blockUser(@PreAuthorizedUser long userId, @RequestParam("blockId") Long blockId){//@PreAuthorizedUser Long userId, @RequestParam("blockId") Long blockId
        commentService.blockUser(userId,blockId);
        return new BaseResponse<>("차단 요청이 성공적으로 처리되었습니다.");
    }
  
    @DeleteMapping("/{comment_id}")
    public BaseResponse<String> dormantUserComment(@PreAuthorizedUser long userId,
                                                   @PathVariable("comment_id") long commentId) {
        log.info("PostController.dormantUserComment");
        log.info("commentId = " + commentId);
        return new BaseResponse<>(commentService.dormantUserComment(userId, commentId));
    }

    @PostMapping("/{comment_id}/report")
    public BaseResponse<String> reportUserComment(@PreAuthorizedUser long userId,
                                                  @PathVariable("comment_id") long commentId,
                                                  @RequestParam("type") String type) {
        log.info("CommentController.reportUserComment");
        return new BaseResponse<>(commentService.reportUserComment(userId, commentId, type));
    }
}
