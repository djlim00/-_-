package com.kuit3.rematicserver.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPunishmentInfo {
    private Long punishmentId;
    //처벌 내용
    private String content;
    //게시판이름
    private String bulletinName;
    //처벌 이유(언어폭력행위)
    private String reason;
    //처벌시작시간
    private Timestamp createdAt;
}
