package com.kuit3.rematicserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginResponse {
    private String jwt;
    private Long user_id;
    private Boolean is_new_user;
}
