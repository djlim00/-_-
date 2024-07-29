package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.common.exception.UnauthorizedUserRequestException;

import com.kuit3.rematicserver.common.exception.UserCommentException;
import com.kuit3.rematicserver.common.response.BaseResponse;

import com.kuit3.rematicserver.dto.post.*;
import com.kuit3.rematicserver.dto.search.SearchPostResponse;
import com.kuit3.rematicserver.service.PostDeletionService;
import org.springframework.web.multipart.MultipartFile;
import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import com.kuit3.rematicserver.dto.post.GetScrolledCommentsResponse;
import com.kuit3.rematicserver.dto.post.PostCommentRequest;
import com.kuit3.rematicserver.dto.post.PostCommentResponse;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("post")
public class PostController {
    private final PostService postService;
    private final PostDeletionService postDeletionService;
    @PostMapping("newpost")
    //    public BaseResponse<CreatePostResponse> createPost(@PreAuthorizedUser long userId,
        public BaseResponse<CreatePostResponse> createPost(
                                                       @RequestBody CreatePostRequest request){
        log.info("PostController::createPost()");

        long userId = 1;

        request.setUser_id(userId);
        return new BaseResponse<>(postService.createPost(request));
    }

    @PostMapping("{post_id}/image")
//    public BaseResponse<UploadPostImageResponse> uploadImage(@PreAuthorizedUser long userId,
    public BaseResponse<UploadPostImageResponse> uploadImage(
                                                             @PathVariable("post_id") Long postId,
                                                             @RequestPart MultipartFile image,
                                                             @RequestPart(required = false) String description){
        log.info("PostController::uploadImage()");

        long userId = 1;

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
        return new BaseResponse<>( postService.getPage(category, lastId));
    }

    @GetMapping("/{postId}")
    //public BaseResponse<GetClickedPostResponse> showClickedPost(@PreAuthorizedUser long userId,
    public BaseResponse<GetClickedPostResponse> showClickedPost(
                                                                @PathVariable long postId) {
        log.info("PostController.showClickedPost");

        long userId = 1;

        return new BaseResponse<>(postService.getValidatedClickedPostInfo(userId, postId));
    }
    @GetMapping("/guest/{postId}")
    public BaseResponse<GetClickedPostResponse> showClickedPostByGuestMode(@PathVariable long postId) {
        log.info("PostController.showClickedPostByGuestMode");
        return new BaseResponse<>(postService.getClickedPostInfo(postId));
    }

    @GetMapping("/comments/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostComments(@PreAuthorizedUser long userId,
                                                                      @PathVariable long postId,
                                                                      @RequestParam String orderBy) {
        log.info("PostController.showPostComments");
        return new BaseResponse<>(postService.getValidatedCommentsByPostId(postId, userId, orderBy));
    }

    @GetMapping("/comments/guest/{postId}")
    public BaseResponse<GetScrolledCommentsResponse> showPostCommentsByGuestMode(@PathVariable long postId,
                                                                                 @RequestParam String orderBy) {
        log.info("PostController.showPostComments");
        return new BaseResponse<>(postService.getCommentsByPostId(postId, orderBy));
    }


    @PostMapping("/comments/{comment_id}")
    public BaseResponse<String> dormantUserComment(@PreAuthorizedUser long userId, @PathVariable("comment_id") long commentId) {
        log.info("PostController.dormantUserComment");
        return new BaseResponse<>(postService.dormantUserComment(userId, commentId));
    }

    @PostMapping("/{post_id}/comment")
    public BaseResponse<PostCommentResponse> leaveNewComment(@PreAuthorizedUser long userId, @PathVariable("post_id") long postId,
                                                             @RequestBody PostCommentRequest request) {
        log.info("PostController.leaveNewComment");
        return new BaseResponse<>(postService.leaveNewComment(userId, postId, request));
    }

    @PostMapping("/comment/{comment_id}/image")
    public BaseResponse<String> uploadCommentImage(@PreAuthorizedUser long userId,
                                                   @PathVariable("comment_id") long commentId,
                                                   @RequestPart MultipartFile image) {
        log.info("PostController.uploadCommentImage");
        if(!postService.isUserMatchesComment(userId, commentId)) {
            throw new UserCommentException(USER_COMMENT_MISMATCH);
        }
        return new BaseResponse<>(postService.uploadCommentImage(userId, commentId, image));
    }

    @DeleteMapping("{postId}")
    public BaseResponse<Object> deletePost(@PreAuthorizedUser long userId, @PathVariable Long postId){
        log.info("PostController::deletePost()");
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
        if(!postService.existsById(postId)){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        if(!postService.checkPostWriter(userId, postId)){
            throw new UnauthorizedUserRequestException(UNAUTHORIZED_USER_REQUEST);
        }
        postService.modifyPost(postId, dto);
        return new BaseResponse<>(null);
    }
}
