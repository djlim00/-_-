package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.exception.DuplicateUserScrapException;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.common.exception.UserScrapNotFoundException;
import com.kuit3.rematicserver.common.response.BaseResponse;

import com.kuit3.rematicserver.dto.post.PostUserScrapResponse;
import com.kuit3.rematicserver.dto.user.PostUserScrapRequest;
import com.kuit3.rematicserver.dto.user.UpdateUserInfoRequest;
import com.kuit3.rematicserver.dto.user.PutNickNameRequest;
import com.kuit3.rematicserver.dto.user.UserMyPageResponse;

import com.kuit3.rematicserver.service.PostService;
import com.kuit3.rematicserver.service.UserScrapService;
import com.kuit3.rematicserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private final UserScrapService userScrapService;
    private final PostService postService;

    @PutMapping("/nickname")
    public BaseResponse<Object> changeNickname(@PreAuthorizedUser long userId, @RequestBody PutNickNameRequest request){
        log.info("UserController::changeNickname()");
        log.info("userid = " + userId);
        log.info("newNickname = " + request.getNickname());
        userService.modifyNickname(userId, request.getNickname());
        return new BaseResponse<>(null);
    }

    @PatchMapping("/mypage/info")
    public BaseResponse<Object> updateUserInfo(@RequestBody UpdateUserInfoRequest request) {
        log.info("UserController::updateUserInfo()");
        log.info("user_id = " + request.getUser_id());
        log.info("nickname = " + request.getNickname());
        log.info("introduction = " + request.getIntroduction());
        log.info("profile_image_url = " + request.getProfile_image_url());
        userService.updateUserInfo(request);
        return new BaseResponse<>(null);
    }

//    public BaseResponse<UserMyPageResponse> getUserInfoPage(@RequestParam long userId)
    @GetMapping("/mypage")
    public BaseResponse<UserMyPageResponse> getUserInfoPage(@PreAuthorizedUser long userId) {
        log.info("UserController.getUserInfoPage");
        return new BaseResponse<>(userService.getMyPageInfo(userId));
    }

    @PostMapping("scraps")
    public BaseResponse<PostUserScrapResponse> postUserScrap(@PreAuthorizedUser long userId,
                                                             @RequestBody PostUserScrapRequest request){
        log.info("UserController::postUserScrap()");
        if(userScrapService.hasDuplicateScrap(userId, request.getPostId())){
            throw new DuplicateUserScrapException(DUPLICATE_USER_SCRAP);
        }
        if(!postService.existsById(request.getPostId())){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        PostUserScrapResponse response = new PostUserScrapResponse(userScrapService.create(userId, request.getPostId()));
        return new BaseResponse<>(response);
    }

    @DeleteMapping("scraps/{scrap_id}")
    public BaseResponse<Object> deleteUserScrap(@PreAuthorizedUser long userId,
                                                @PathVariable("scrap_id") Long scrapId){
        log.info("UserController::deleteUserScrap()");
        log.info("scrapId=" + scrapId);

        if(!userScrapService.isScrapCreatedByUser(userId, scrapId)){
            throw new UserScrapNotFoundException(USER_SCRAP_NOT_FOUND);
        }
        userScrapService.deleteById(scrapId);
        return new BaseResponse<>(null);
    }
}
