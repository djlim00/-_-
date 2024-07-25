package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.common.exception.UnauthorizedUserRequestException;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.CreatePostRequest;
import com.kuit3.rematicserver.dto.CreatePostResponse;

import com.kuit3.rematicserver.dto.UploadPostImageResponse;
import com.kuit3.rematicserver.dto.post.GetSearchPostDto;
import com.kuit3.rematicserver.dto.search.GetSearchPostResponse;
import com.kuit3.rematicserver.service.PostDeletionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.multipart.MultipartFile;
import com.kuit3.rematicserver.common.response.BaseResponse;

import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import com.kuit3.rematicserver.dto.post.GetScrolledCommentsResponse;
import com.kuit3.rematicserver.dto.search.GetSearchPostResponse;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.POST_NOT_FOUND;
import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.UNAUTHORIZED_USER_REQUEST;

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
        request.setUser_id(userId);
        return new BaseResponse<>(postService.createPost(request));
    }

    @PostMapping("{post_id}/image")
    public BaseResponse<UploadPostImageResponse> uploadImage(@PreAuthorizedUser long userId,
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
        UploadPostImageResponse response = new UploadPostImageResponse(postService.uploadImage(postId, image, description));
        return new BaseResponse<>(response);
    }

    @GetMapping
    public BaseResponse<GetSearchPostResponse> showPage(@RequestParam String category,
                                                        @RequestParam(required = false) Long lastId){
        log.info("PostController::showPage()");
        return new BaseResponse<>( postService.getPage(category, lastId));
    }

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

    @DeleteMapping("{postId}")
    public BaseResponse<Object> deletePost(@PreAuthorizedUser long userId, @PathVariable Long postId){
        log.info("PostController::deletePost()");
        if(!postService.hasPostWithId(postId)){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        if(!postService.checkPostWriter(userId, postId)){
            throw new UnauthorizedUserRequestException(UNAUTHORIZED_USER_REQUEST);
        }
        postDeletionService.handle(postId);
        return new BaseResponse<>(null);
    }
}
