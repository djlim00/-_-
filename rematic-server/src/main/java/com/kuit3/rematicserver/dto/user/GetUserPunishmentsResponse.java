package com.kuit3.rematicserver.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserPunishmentsResponse {
    private String nickName;
    private Long userId;
    private List<UserPunishmentInfo> punishmentInfos;
}
