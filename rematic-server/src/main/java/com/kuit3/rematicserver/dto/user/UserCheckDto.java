package com.kuit3.rematicserver.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UserCheckDto {
    int userCount;
    int isActive;
}
