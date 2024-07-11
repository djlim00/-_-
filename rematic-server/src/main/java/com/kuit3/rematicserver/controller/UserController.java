package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.argument_resolver.PreAuthorizedUser;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.PutNickNameRequest;
import com.kuit3.rematicserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
