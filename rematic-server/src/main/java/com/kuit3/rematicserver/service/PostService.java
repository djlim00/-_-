package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostInfoDao;
import com.kuit3.rematicserver.dao.RecentKeywordDao;
import com.kuit3.rematicserver.dto.post.GetPostDto;
import com.kuit3.rematicserver.dto.search.GetSearchResultResponse;
import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDaoImpl;
    private final RecentKeywordDao recentKeywordDaoImpl;
    private final PostInfoDao postInfoDao;

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

    public GetClickedPostResponse getClickedPostInfo(long postId) {
        log.info("BulletinService.getClickedPostInfo");
        //게시물이 삭제되진 않았는지 확인
        if(!checkPostExists(postId)) {
            throw new DatabaseException(POST_NOT_FOUND);
        }
        //게시물 익명성 여부 확인
        GetClickedPostResponse postResponse = new GetClickedPostResponse();
        if(!checkAnonymity(postId)) {
            postResponse.setUserInfo(new UserInfo("익명", null));
        } else {
            postResponse.setUserInfo(postInfoDao.getWriterInfo(postId));
        }
        //이외 정보 가져오기
        postResponse.setBulletinName(postInfoDao.getBulletinInfo(postId));
        postResponse.setPostInfo(postInfoDao.getPostInfo(postId));
        //이미지 주소와 설명 가져오기
        if(!postInfoDao.imageExists(postId)) {
            postResponse.setImageInfo(null);
        } else {
            postResponse.setImageInfo(postInfoDao.getImageInfo(postId));
        }

        return postResponse;
    }

    private boolean checkAnonymity(long postId) {
        return postInfoDao.isPostAnonymous(postId);
    }

    private boolean checkPostExists(long postId) {
        return postInfoDao.isPostExists(postId);
    }

//    public GetScrolledCommentsResponse getCommentsByPostId(long postId, long lastId, String orderBy) {
//        log.info("BulletinService.getCommentsByPostId");
//
//    }

}
