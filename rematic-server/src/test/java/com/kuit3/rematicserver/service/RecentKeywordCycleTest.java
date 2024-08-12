package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.RecentKeywordDaoImpl;
import com.kuit3.rematicserver.dao.SearchDao;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import com.kuit3.rematicserver.oauth.KakaoAuthApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class RecentKeywordCycleTest {

    @MockBean
    private AuthService authService;
    @MockBean
    private KakaoAuthApiClient kakaoAuthApiClient;

    @Autowired
    private RecentKeywordDaoImpl recentKeywordDao;
    @Autowired
    private SearchDao searchDao;

    @Test
    @DisplayName("최근 검색어 저장 및 조회")
    void searchWordSaveAndCheck() {
        long userId = 0L;
        //검색어 3개 등록 후 두번째 검색어를 삭제했을 때 두번째 검색어가 3으로 보이는 지 실험
        //given
        long firstWord = recentKeywordDao.saveKeyword(userId, "사용자 검색어 1");
        long secondWord = recentKeywordDao.saveKeyword(userId, "사용자 검색어 2");
        long thirdWord = recentKeywordDao.saveKeyword(userId, "사용자 검색어 3");
        //when
        searchDao.modifyUserRecentKeyword(userId, secondWord);
        //then
        List<UserRecentKeywordResponse> result = searchDao.getKeywordsByUserId(userId);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1).getKeyWord()).isEqualTo("사용자 검색어 3");
    }
}
