package com.kuit3.rematicserver.oauth;

import com.kuit3.rematicserver.common.exception.auth.OAuthApiRequestFailedException;
import com.kuit3.rematicserver.dto.auth.KakaoTokens;
import com.kuit3.rematicserver.dto.auth.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.OAUTH_API_REQUEST_FAILED;


@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAuthApiClient {
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-url}")
    private String redirectUrl;

    private final RestTemplate restTemplate;

    public String requestAccessToken(String code) {
        log.info("KakaoAuthApiClient::requestAccessToken()");
        log.info("code = " + code);
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUrl);
        body.add("client_id", clientId);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        KakaoTokens response;
        try {
            response = restTemplate.postForObject(authUrl, request, KakaoTokens.class);
        }
        catch (Exception e){
            throw new OAuthApiRequestFailedException(OAUTH_API_REQUEST_FAILED);
        }
        return response.getAccessToken();
    }

    public KakaoUserInfoResponse requestAuthInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer "+ accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        KakaoUserInfoResponse response;
        try {
            response = restTemplate.postForObject(apiUrl, request, KakaoUserInfoResponse.class);
        }
        catch (Exception e){
            throw new OAuthApiRequestFailedException(OAUTH_API_REQUEST_FAILED);
        }
        return response;
    }
}
