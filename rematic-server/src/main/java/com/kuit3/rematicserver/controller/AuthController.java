package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.dto.LoginResponse;
import com.kuit3.rematicserver.dto.auth.KakaoLoginRequest;
import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("kakao")
    public BaseResponse<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request){
        log.info("AuthController::kakaoLogin()");
        return new BaseResponse<>(authService.kakaoLogin(request));
    }

    @PostMapping("naver")
    public BaseResponse<LoginResponse> naverLogin(){
        return null;
    }

}
