package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.common.exception.PunishReasonException;
import com.kuit3.rematicserver.common.exception.UserDormantException;
import com.kuit3.rematicserver.common.exception.UserNotFoundException;
import com.kuit3.rematicserver.dao.BulletinDaoImpl;
import com.kuit3.rematicserver.dao.UserDao;

import com.kuit3.rematicserver.dto.user.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final BulletinDaoImpl bulletinDao;
    public void modifyNickname(long userId, String newNickname) {
        int affectedRow = userDao.changeUserNickname(userId, newNickname);
        if(affectedRow != 1){
            throw new DatabaseException(DATABASE_ERROR);
        }
    }


    public void updateUserInfo(UpdateUserInfoRequest request) {
        int affectedRow = userDao.updateUserInfo(request);
        if (affectedRow != 1) {
            throw new RuntimeException("User info update failed");
        }
    }

    public UserMyPageResponse getMyPageInfo(long userId) {
        log.info("UserService.getMyPageInfo");
        checkUserExistsOrDormant(userId);
        return userDao.getMyPageInfo(userId);
    }

    private void checkUserExistsOrDormant(long userId) {
        UserCheckDto result = userDao.checkExistsOrDormant(userId);
        if(result.getUserCount() == 0) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        if(result.getIsActive() == 0) {
            throw new UserDormantException(USER_DORMANT_STATUS);
        }
    }

    public void deleteUser(long userId) {
        log.info("UserService::deleteUser()");
        checkUserExistsOrDormant(userId);
        userDao.modifyStatus(userId, "dormant"); // 회원 탈퇴시에 dormant로 변경하는 게 맞는가?
    }

    public GetUserPunishmentsResponse getUserPunishmentList(long userId) {
        log.info("UserService.getUserPunishmentList");
        checkUserExistsOrDormant(userId);
        GetUserPunishmentsResponse response = new GetUserPunishmentsResponse();
        response.setUserId(userId);
        response.setNickName(userDao.getUserNickName(userId));
        List<UserPunishmentInfo> infoList = userDao.getUserPunishmentsList(userId);
        for(UserPunishmentInfo info : infoList) {
            String target = info.getReason();
            switch (target) {
                case "abuse" -> info.setReason("욕설/비하");
                case "obscene" -> info.setReason("음란물/불건전 게시물");
                case "unrelated" -> info.setReason("게시판 성격 부적절");
                case "advertisement" -> info.setReason("상업성 광고 및 판매");
            }
        }
        response.setPunishmentInfos(infoList);
        return response;
    }

    public GetUserPunishmentValidResponse validateUserPunishment(long userId) {
        log.info("UserService.validateUserPunishment");
        checkUserExistsOrDormant(userId);
        GetUserPunishmentValidResponse response = new GetUserPunishmentValidResponse();
        List<Boolean> result = userDao.validateUserPunishment(userId);
        if (result.contains(true)) {
            response.setIsRestricted(true);
        } else {
            response.setIsRestricted(false);
        }
        response.setUserId(userId);
        return response;
    }
}
