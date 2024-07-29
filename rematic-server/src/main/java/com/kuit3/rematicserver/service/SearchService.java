package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.common.exception.UserKeywordException;
import com.kuit3.rematicserver.dao.SearchDao;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import com.kuit3.rematicserver.dto.search.UserRecommendableKeywordsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.DATABASE_ERROR;
import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.USER_KEYWORD_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final SearchDao searchDao;

    public List<UserRecentKeywordResponse> getUserRecentKeywords(long userId) {
        log.info("SearchService.getUserRecentKeywords");
        return searchDao.getKeywordsByUserId(userId);
    }

    public List<UserRecommendableKeywordsResponse> getUserRecommendableKeywords(long userId) {
        log.info("SearchService.getUserRecommendableKeywords");
        Long userRecentVisitedBulletin = validateUserRecentVisitedBulletin(userId);
        if(userRecentVisitedBulletin != null) {
            List<UserRecommendableKeywordsResponse> sameBulletinList = searchDao.getSameBulletinList(userRecentVisitedBulletin);
            List<UserRecommendableKeywordsResponse> sameGenreList = searchDao.getSameGenreList(userRecentVisitedBulletin);
            List<UserRecommendableKeywordsResponse> finalList = new ArrayList<>();
            finalList.addAll(sameBulletinList);
            finalList.addAll(sameGenreList);
            return finalList;
        }
        else {
            return searchDao.getFourRandomWorks();
        }
    }

    private Long validateUserRecentVisitedBulletin(long userId) {
        log.info("SearchService.validateUserRecentVisitedBulletin");
        return searchDao.hasUserRecentVisitedBulletin(userId);
    }


    public String deactivateUserKeyword(long userId, long keywordId) {
        log.info("SearchService.deactivateUserKeyword");
        if(!searchDao.checkUserRecentKeyword(userId, keywordId)) {
            throw new UserKeywordException(USER_KEYWORD_NOT_FOUND);
        }
        int isSuccess = searchDao.modifyUserRecentKeyword(userId, keywordId);
        if(isSuccess == 1) {
            return "deleted keyword successfully";
        }
        else {
            throw new DatabaseException(DATABASE_ERROR);
        }
    }
}
