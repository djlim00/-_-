package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.UserKeywordException;
import com.kuit3.rematicserver.dao.RecentKeywordDaoImpl;
import com.kuit3.rematicserver.dao.SearchDao;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private SearchDao searchDao;

    @Mock
    private RecentKeywordDaoImpl recentKeywordDao;

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
        long userId = 0L;
        long keywordId = 0L;
        //given
        given(searchDao.checkUserRecentKeyword(userId, keywordId)).willReturn(false);
        //then
        assertThrows(UserKeywordException.class, () -> {
            searchService.deactivateUserKeyword(userId, keywordId);
        });
    }

    @Test
    @Transactional
    @DisplayName("최근 검색어 저장 및 조회")
    void searchWordSaveAndCheck() {
        long userId = 0L;
        //검색어 3개 등록 후 두번째 검색어를 삭제했을 때 두번째 검색어가 3으로 보이는 지 실험
        //given
        recentKeywordDao.saveKeyword(userId, "사용자 검색어 1");
        recentKeywordDao.saveKeyword(userId, "사용자 검색어 2");
        recentKeywordDao.saveKeyword(userId, "사용자 검색어 3");
        //when
        when(searchDao.modifyUserRecentKeyword(userId, 1L)).thenReturn(1);
        //then
        List<UserRecentKeywordResponse> result = searchDao.getKeywordsByUserId(userId);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(1).getKeyWordId());
        assert(result.get(1).getKeyWord()).equals("사용자 검색어 3");
    }

}

