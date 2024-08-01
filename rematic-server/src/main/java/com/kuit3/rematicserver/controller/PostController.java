package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.common.exception.UnauthorizedUserRequestException;
import com.kuit3.rematicserver.common.exception.UserCommentException;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.post.*;
import com.kuit3.rematicserver.dto.search.SearchPostResponse;
import com.kuit3.rematicserver.service.PostDeletionService;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("post")
public class PostController {
    private final PostService postService;
    private final PostDeletionService postDeletionService;
    @PostMapping("newpost")
    public BaseResponse<CreatePostResponse> createPost(@PreAuthorizedUser long userId, @RequestBody CreatePostRequest request){
        log.info("PostController::createPost()");

        log.info("userId=" + userId);
        log.info("request = " + request);
        request.setUser_id(userId);
        return new BaseResponse<>(postService.createPost(request));
    }

    @PostMapping("{post_id}/image")
    public BaseResponse<UploadPostImageResponse> uploadImage(@PreAuthorizedUser long userId,
                                                             @PathVariable("post_id") Long postId,
                                                             @RequestPart MultipartFile image,
                                                             @RequestPart(required = false) String description){
        log.info("PostController::uploadImage()");
        log.info("userId=" + userId);
        log.info("postId=" + postId);

        if(!postService.existsById(postId)){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        if(!postService.checkPostWriter(userId, postId)){
            throw new UnauthorizedUserRequestException(UNAUTHORIZED_USER_REQUEST);
        }
        UploadPostImageResponse response = new UploadPostImageResponse(postService.uploadImage(postId, image, description));
        return new BaseResponse<>(response);
    }

    @GetMapping
    public BaseResponse<SearchPostResponse> showPage(@RequestParam String category,
                                                     @RequestParam(required = false) Long lastId){
        log.info("PostController::showPage()");
        log.info("category=" + category);
        log.info("lastId=" + lastId);

        return new BaseResponse<>( postService.getPage(category, lastId));
    }
    @GetMapping("/{postId}")
    public BaseResponse<GetClickedPostResponse> showClickedPost(@PreAuthorizedUser long userId,
                                                                @PathVariable long postId) {
        log.info("PostController.showClickedPost");
        log.info("userId = " + userId);
        log.info("postId = " + postId);
        return new BaseResponse<>(postService.getValidatedClickedPostInfo(userId, postId));
    }
    @GetMapping("/guest/{postId}")
    public BaseResponse<GetClickedPostResponse> showClickedPostByGuestMode(@PathVariable long postId) {
        log.info("PostController.showClickedPostByGuestMode");
        log.info("postId = " + postId);

        return new BaseResponse<>(postService.getClickedPostInfo(postId));
    }

    @GetMapping("/comments/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostComments(@PreAuthorizedUser long userId,
                                                                      @PathVariable long postId,
                                                                      @RequestParam String orderBy) {
        log.info("PostController.showPostComments");
        log.info("userId = " + userId);
        log.info("postId = " + postId);
        return new BaseResponse<>(postService.getValidatedCommentsByPostId(postId, userId, orderBy));
    }

    @GetMapping("/comments/guest/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostCommentsByGuestMode(@PathVariable long postId,
                                                                                 @RequestParam String orderBy) {
        log.info("PostController.showPostCommentsByGuestMode");
        log.info("postId = " + postId);

        return new BaseResponse<>(postService.getCommentsByPostId(postId, orderBy));
    }

    @PostMapping("/{post_id}/comment")
    public BaseResponse<PostCommentResponse> leaveNewComment(@PreAuthorizedUser long userId,
                                                             @PathVariable("post_id") long postId,
                                                             @RequestBody PostCommentRequest request) {
        log.info("PostController.leaveNewComment");
        log.info("userId = " + userId);
        log.info("postId = " + postId);

        return new BaseResponse<>(postService.leaveNewComment(userId, postId, request));
    }

    @PostMapping("/comment/{comment_id}/image")
    public BaseResponse<String> uploadCommentImage(@PreAuthorizedUser long userId,
                                                   @PathVariable("comment_id") long commentId,
                                                   @RequestPart MultipartFile image) {
        log.info("PostController.uploadCommentImage");
        log.info("userId = " + userId);
        log.info("postId = " + commentId);

        if(!postService.isUserMatchesComment(userId, commentId)) {
            throw new UserCommentException(USER_COMMENT_MISMATCH);
        }
        return new BaseResponse<>(postService.uploadCommentImage(userId, commentId, image));
    }

    @DeleteMapping("{postId}")
    public BaseResponse<Object> deletePost(@PreAuthorizedUser long userId, @PathVariable Long postId){
        log.info("PostController::deletePost()");
        log.info("userId = " + userId);
        log.info("postId = " + postId);

        if(!postService.existsById(postId)){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        if(!postService.checkPostWriter(userId, postId)){
            throw new UnauthorizedUserRequestException(UNAUTHORIZED_USER_REQUEST);
        }
        postDeletionService.handle(postId);
        return new BaseResponse<>(null);
    }

    @GetMapping("{post_id}/updateform")
    public BaseResponse<GetPostUpdateFormDto> getUpdateForm(@PreAuthorizedUser long userId,
                                                            @PathVariable("post_id") Long postId){
        log.info("PostController::getUpdateForm()");
        log.info("userId = " + userId);
        log.info("postId = " + postId);

        if(!postService.existsById(postId)){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        if(!postService.checkPostWriter(userId, postId)){
            throw new UnauthorizedUserRequestException(UNAUTHORIZED_USER_REQUEST);
        }
        return new BaseResponse<>(postService.getPostUpdateForm(postId));
    }

    @PatchMapping("{post_id}")
    public BaseResponse<Object> patchPost(@PreAuthorizedUser long userId,
                                          @PathVariable("post_id") Long postId,
                                          @RequestBody PatchPostDto dto){
        log.info("PostController::updatePost()");
        log.info("userId = " + userId);
        log.info("postId = " + postId);

        postService.modifyPost(postId, dto);
        return new BaseResponse<>(null);
    }
}
