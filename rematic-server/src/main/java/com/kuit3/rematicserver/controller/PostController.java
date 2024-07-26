package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.common.exception.UnauthorizedUserRequestException;
import com.kuit3.rematicserver.dto.CreatePostResponse;
import com.kuit3.rematicserver.dto.CreatePostRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.multipart.MultipartFile;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.POST_NOT_FOUND;
import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.UNAUTHORIZED_USER_REQUEST;
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
@RequestMapping("post")
public class PostController {
    private final PostService postService;
    @PostMapping("newpost")
    public BaseResponse<CreatePostResponse> createPost(@PreAuthorizedUser long userId, @RequestBody CreatePostRequest request){
        log.info("PostController::createPost()");
        request.setUser_id(userId);
        return new BaseResponse<>(postService.createPost(request));
    }

    @PostMapping("{post_id}/image")
    public BaseResponse<Object> postImage(@PreAuthorizedUser long userId,
                                          @PathVariable("post_id") Long postId,
                                          @RequestPart MultipartFile image,
                                          @RequestPart(required = false) String description){
        log.info("PostController::uploadImage()");
        if(!postService.hasPostWithId(postId)){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        if(!postService.checkPostWriter(userId, postId)){
            throw new UnauthorizedUserRequestException(UNAUTHORIZED_USER_REQUEST);
        }
        postService.uploadImage(postId, image, description);
        return new BaseResponse<>(null);
    }

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

    @PostMapping("/{post_id}/like/up")
    public BaseResponse<String> increaseLike(@PathVariable("post_id") Long postId) {
        postService.increaseLike(postId);
        return new BaseResponse<>("좋아요 증가");
    }

    @PostMapping("/{post_id}/like/down")
    public BaseResponse<String> decreaseLike(@PathVariable("post_id") Long postId) {
        postService.decreaseLike(postId);
        return new BaseResponse<>("좋아요 감소");
    }

    @PostMapping("/{post_id}/hate/up")
    public BaseResponse<String> increaseHate(@PathVariable("post_id") Long postId) {
        postService.increaseHate(postId);
        return new BaseResponse<>("싫어요 증가");
    }

    @PostMapping("/{post_id}/hate/down")
    public BaseResponse<String> decreaseHate(@PathVariable("post_id") Long postId) {
        postService.decreaseHate(postId);
        return new BaseResponse<>("싫어요 감소");
    }
}
