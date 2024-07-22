package com.kuit3.rematicserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class CreateUserDTO {
    private String email;
    private String nickname;
    private String profile_image_url;
}
