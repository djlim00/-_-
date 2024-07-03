package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.DATABASE_ERROR;

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
}
