package com.kuit3.rematicserver.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoRequest {
    private Long user_id;
    private String nickname;
    private String introduction;
    private String profile_image_url;
}
