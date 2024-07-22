package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.common.exception.UserDormantException;
import com.kuit3.rematicserver.common.exception.UserNotFoundException;
import com.kuit3.rematicserver.dao.UserDao;

import com.kuit3.rematicserver.dto.UpdateUserInfoRequest;
import com.kuit3.rematicserver.dto.user.UserCheckDto;
import com.kuit3.rematicserver.dto.user.UserMyPageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
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
}
