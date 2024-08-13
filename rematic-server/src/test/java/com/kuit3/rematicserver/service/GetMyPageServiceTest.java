package com.kuit3.rematicserver.service;


import com.kuit3.rematicserver.dao.UserDao;
import com.kuit3.rematicserver.dto.user.UserCheckDto;
import com.kuit3.rematicserver.dto.user.UserMyPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class GetMyPageServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;

    @DisplayName("마이 페이지 정보 가져오기")
    @Test
    @Transactional
    void getMyPageUserInfo() {
        //given
        Long userId = 1L;
        UserMyPageResponse user = new UserMyPageResponse(
                "testNickName", "test.jpg", "testIntroduction",
                0L, 0L,0L);
        UserCheckDto userExistence = new UserCheckDto(1, 1);
        given(userDao.checkExistsOrDormant(userId)).willReturn(userExistence);
        given(userDao.getMyPageInfo(userId)).willReturn(user);
        //when
        UserMyPageResponse myPageInfo = userService.getMyPageInfo(userId);
        //then
        assertEquals("testNickName", myPageInfo.getNickName());
        assertEquals("test.jpg", myPageInfo.getImageUrl());
        assertEquals(0L, myPageInfo.getMyPosts());
    }

}
