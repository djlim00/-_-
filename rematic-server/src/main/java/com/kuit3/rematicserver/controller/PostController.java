package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.common.exception.UnauthorizedUserRequestException;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.CreatePostResponse;
import com.kuit3.rematicserver.dto.CreatePostRequest;
import com.kuit3.rematicserver.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
}
