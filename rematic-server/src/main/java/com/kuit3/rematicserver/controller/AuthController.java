package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.dto.KakaoSignUpReqeust;
import com.kuit3.rematicserver.dto.LoginResponse;
import com.kuit3.rematicserver.dto.OAuthLoginResponse;
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

    // ios sdk 와 연동되는 방식
    @PostMapping("kakao/login")
    public BaseResponse<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request){
       log.info("AuthController::kakaoLogin()");
        return new BaseResponse<>(authService.kakaoLogin(request));
    }

    @PostMapping("kakao/signup")
    public BaseResponse<LoginResponse> kakaoSignUp(@RequestBody KakaoSignUpReqeust request){
        log.info("AuthController::kakaoSignUp()");
        return new BaseResponse<>( authService.kakaoSignup(request));
    }

    // rest api  방식
    @GetMapping("kakao")
    public BaseResponse<OAuthLoginResponse> kakaoLogin(@RequestParam String code){
        log.info("AuthController::kakaoLogin()");
        return new BaseResponse<>(authService.kakaoOAuthLogin(code));
    }
}
