package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.UserKeywordException;
import com.kuit3.rematicserver.dao.SearchDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private SearchDao searchDao;

    @Test
    @Transactional
    @DisplayName("최근 검색어 존재시 검색어 삭제")
    void keywordDeletionTest() {
        //given
        Long userId = 0L;
        Long keywordId = 0L;
        //키워드가 있는 상황 산정
        given(searchDao.checkUserRecentKeyword(userId, keywordId)).willReturn(true);
        given(searchDao.modifyUserRecentKeyword(userId, keywordId)).willReturn(1);
        //when
        String consequence = searchService.deactivateUserKeyword(userId, keywordId);
        //then
        assertEquals(consequence, "deleted keyword successfully");
    }

    @Test
    @Transactional
    @DisplayName("최근 검색어가 없을 때 검색어 삭제")
    void keywordDeletionFailTest() {
        Long userId = 0L;
        Long keywordId = 0L;
        //given
        given(searchDao.checkUserRecentKeyword(userId, keywordId)).willReturn(false);
        //then
        assertThrows(UserKeywordException.class, () -> {
            searchService.deactivateUserKeyword(userId, keywordId);
        });
    }

}

