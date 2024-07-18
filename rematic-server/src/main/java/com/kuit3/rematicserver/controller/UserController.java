package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.user.PutNickNameRequest;
import com.kuit3.rematicserver.dto.user.UserMyPageResponse;
import com.kuit3.rematicserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    @PutMapping("/nickname")
    public BaseResponse<Object> changeNickname(@PreAuthorizedUser long userId, @RequestBody PutNickNameRequest request){
        log.info("UserController::changeNickname()");
        log.info("userid = " + userId);
        log.info("newNickname = " + request.getNickname());
        userService.modifyNickname(userId, request.getNickname());
        return new BaseResponse<>(null);
    }

//    public BaseResponse<UserMyPageResponse> getUserInfoPage(@RequestParam long userId)
    @GetMapping("/mypage")
    public BaseResponse<UserMyPageResponse> getUserInfoPage(@PreAuthorizedUser long userId) {
        log.info("UserController.getUserInfoPage");
        return new BaseResponse<>(userService.getMyPageInfo(userId));
    }
}
