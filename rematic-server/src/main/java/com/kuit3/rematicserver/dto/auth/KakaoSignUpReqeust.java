package com.kuit3.rematicserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoSignUpReqeust {
    String kakao_id;
    String nickname;
    String profile_image_url;
}
