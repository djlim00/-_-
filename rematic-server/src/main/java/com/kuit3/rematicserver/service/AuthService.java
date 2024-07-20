package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.UserDuplicateEmailException;
import com.kuit3.rematicserver.common.exception.UserDuplicateNicknameException;
import com.kuit3.rematicserver.common.exception.UserNotFoundException;
import com.kuit3.rematicserver.dao.UserDao;
import com.kuit3.rematicserver.dto.CreateUserDTO;
import com.kuit3.rematicserver.dto.KakaoSignUpReqeust;
import com.kuit3.rematicserver.dto.LoginResponse;
import com.kuit3.rematicserver.dto.OAuthLoginResponse;
import com.kuit3.rematicserver.dto.auth.KakaoLoginRequest;
import com.kuit3.rematicserver.dto.auth.KakaoUserInfoResponse;
import com.kuit3.rematicserver.jwt.JwtProvider;
import com.kuit3.rematicserver.service.auth.KakaoAuthApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final UserDao userDao;
    private final JwtProvider jwtProvider;

    public OAuthLoginResponse kakaoOAuthLogin(String code) {
        log.info("AuthService::kakaoLogin()");
        String accessToken = kakaoAuthApiClient.requestAccessToken(code);
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
        return new OAuthLoginResponse(token, userId, isNewUser);
    }

    public long getUserIdByEmail(String email){
        return userDao.getUserIdByEmail(email);
    }

    public LoginResponse kakaoLogin(KakaoLoginRequest request) {
        log.info("AuthService::kakaoLogin()");

        if(!userDao.hasUserWithDuplicateEmail(request.getKakao_id())){
            throw new UserNotFoundException(USER_NOT_FOUND);
        }

        long userId = getUserIdByEmail(request.getKakao_id());
        String token = jwtProvider.createToken(request.getKakao_id(), userId);
        return new LoginResponse(token, userId);
    }

    public LoginResponse kakaoSignup(KakaoSignUpReqeust request) {
        log.info("AuthService::kakaoSignup()");

        if(userDao.hasUserWithDuplicateEmail(request.getKakao_id())){
            throw new UserDuplicateEmailException(DUPLICATE_EMAIL);
        }
        if(userDao.hasUserWithDuplicateNickname(request.getNickname())){
            throw new UserDuplicateNicknameException(DUPLICATE_NICKNAME);
        }

        CreateUserDTO dto = CreateUserDTO.builder()
                .email(request.getKakao_id())
                .nickname(request.getNickname())
                .profile_image_url(request.getProfile_image_url())
                .build();
        long userId = userDao.createUser(dto);
        String token = jwtProvider.createToken(request.getKakao_id(), userId);
        return new LoginResponse(token, userId);
    }
}
