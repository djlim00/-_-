package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.dao.SearchDao;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.DATABASE_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final SearchDao searchDao;

    public List<UserRecentKeywordResponse> getUserRecentKeywords(long userId) {
        log.info("SearchService.getUserRecentKeywords");
        return searchDao.getKeywordsByUserId(userId);
    }

//    public UserRecommendableKeywordsResponse getUserRecommendableKeywords(long userId) {
//        log.info("SearchService.getUserRecommendableKeywords");
//        UserRecommendableKeywordsResponse keywordsResponse = new UserRecommendableKeywordsResponse();
//        keywordsResponse.setImageUrl(searchDao.getTopPostPicUrl());
//        List<Work> works = new ArrayList<>();
//        List<Work> topPostInfo = searchDao.getTopPostWorkInfo();
//        works.add(topPostInfo);
//    }

    public String deactivateUserKeyword(long userId, long keywordId) {
        log.info("SearchService.deactivateUserKeyword");
        int isSuccess = searchDao.modifyUserRecentKeyword(userId, keywordId);
        if(isSuccess == 1) {
            return "deleted keyword successfully";
        }
        else {
            throw new DatabaseException(DATABASE_ERROR);
        }
    }


}