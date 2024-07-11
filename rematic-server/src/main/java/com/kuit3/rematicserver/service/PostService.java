package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.RecentKeywordDao;
import com.kuit3.rematicserver.dto.GetPostDto;
import com.kuit3.rematicserver.dto.GetSearchResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDaoImpl;
    private final RecentKeywordDao recentKeywordDaoImpl;

    @Transactional
    public GetSearchResultResponse searchPageByKeywordAndCategory(Long userId, String keyword, String category, Long lastId) {
        log.info("PostService::getPageByKeywordAndCategory()");
        List<GetPostDto> page = postDaoImpl.getPage(keyword, category, lastId);
        boolean hasNext = checkNextPage(keyword, category, page);
        recentKeywordDaoImpl.saveKeyword(userId, keyword);
        return new GetSearchResultResponse(page, hasNext);
    }

    private boolean checkNextPage(String keyword, String category, List<GetPostDto> page) {
        boolean hasNext = false;
        if(page.size() > 0){
            Long nextStartingId = page.get(page.size() - 1).getPost_id();
            log.info("nextStartingId = " + nextStartingId);
            hasNext = postDaoImpl.hasNextPage(keyword, category, nextStartingId);
        }
        return hasNext;
    }

    public GetSearchResultResponse searchPageByKeywordAndCategory_guestmode(String keyword, String category, Long lastId) {
        log.info("PostService::getPageByKeywordAndCategory()");
        List<GetPostDto> page = postDaoImpl.getPage(keyword, category, lastId);
        boolean hasNext = checkNextPage(keyword, category, page);
        return new GetSearchResultResponse(page, hasNext);
    }
}
