package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.jwt.unauthorized.JwtInvalidTokenException;
import com.kuit3.rematicserver.dao.UserDao;
import com.kuit3.rematicserver.dto.CreateUserDTO;
import com.kuit3.rematicserver.dto.LoginResponse;
import com.kuit3.rematicserver.dto.auth.KakaoLoginRequest;
import com.kuit3.rematicserver.dto.auth.KakaoUserInfoResponse;
import com.kuit3.rematicserver.jwt.JwtProvider;
import com.kuit3.rematicserver.service.auth.KakaoAuthApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.TOKEN_MISMATCH;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final UserDao userDao;
    private final JwtProvider jwtProvider;
    public LoginResponse kakaoLogin(KakaoLoginRequest request) {
        log.info("AuthService::kakaoLogin()");
        String accessToken = kakaoAuthApiClient.requestAccessToken(request.getCode());
        log.info("accessToken = " + accessToken);

        KakaoUserInfoResponse userInfoResponse = kakaoAuthApiClient.requestAuthInfo(accessToken);
        log.info("userInfoResponse = " + userInfoResponse);
        log.info("email = "  + userInfoResponse.getEmail());

        long userId;
        Boolean isNewUser;
        if(!userDao.hasUserWithDuplicateEmail(userInfoResponse.getEmail())){
            userId = userDao.createUser(CreateUserDTO.builder()
                    .email(userInfoResponse.getEmail())
                    .nickname(userInfoResponse.getNickname())
                    .profile_image_url(userInfoResponse.getProfile_image_url())
                    .build());
            isNewUser = true;
        }
        else{
            userId = getUserIdByEmail(userInfoResponse.getEmail());
            isNewUser = false;
        }
        String token = jwtProvider.createToken(userInfoResponse.getEmail(), userId);
        return new LoginResponse(token, userId, isNewUser);
    }

    public long getUserIdByEmail(String email){
        try{
            return userDao.getUserIdByEmail(email);
        }
        catch (Exception e){
            throw new JwtInvalidTokenException(TOKEN_MISMATCH);
        }
    }
}
