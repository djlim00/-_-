package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostInfoDao;
import com.kuit3.rematicserver.dao.RecentKeywordDao;
import com.kuit3.rematicserver.dto.post.GetPostDto;
import com.kuit3.rematicserver.dto.post.GetScrolledCommentsResponse;
import com.kuit3.rematicserver.dto.post.commentresponse.CommentInfo;
import com.kuit3.rematicserver.dto.post.commentresponse.FamilyComment;
import com.kuit3.rematicserver.dto.post.postresponse.PostInfo;
import com.kuit3.rematicserver.dto.search.GetSearchResultResponse;
import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    //로그인 사용자용
    public GetClickedPostResponse getValidatedClickedPostInfo(long userId, long postId) {
        log.info("PostService.getValidatedClickedPostInfo");
        //게시물이 삭제되진 않았는지 확인
        if(!checkPostExists(postId)) {
            throw new DatabaseException(POST_NOT_FOUND);
        }
        GetClickedPostResponse postResponse = new GetClickedPostResponse();
        //게시물 익명성 여부 확인
        validateAndSetPostWriter(postId, postResponse);
        //게시글 정보 가져오기
        postResponse.setBulletinName(postInfoDao.getBulletinInfo(postId));
        //이미지 주소와 설명 가져오기
        if(!postInfoDao.imageExists(postId)) {
            postResponse.setImageInfo(null);
        } else {
            postResponse.setImageInfo(postInfoDao.getImageInfo(postId));
        }
        PostInfo postInfo = postInfoDao.getPostInfo(postId);
        List<Boolean> userPreference = postInfoDao.checkUserPrefer(userId, postId);
        postInfo.setIsLiked(userPreference.get(0));
        postInfo.setIsHated(userPreference.get(1));
        postInfo.setIsScraped(userPreference.get(2));
        postResponse.setPostInfo(postInfo);
        return postResponse;
    }

    //게스트용
    public GetClickedPostResponse getClickedPostInfo(long postId) {
        log.info("PostService.getClickedPostInfo");
        //게시물이 삭제되진 않았는지 확인
        if(!checkPostExists(postId)) {
            throw new DatabaseException(POST_NOT_FOUND);
        }
        //게시물 익명성 여부 확인
        GetClickedPostResponse postResponse = new GetClickedPostResponse();
        validateAndSetPostWriter(postId, postResponse);
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

    private void validateAndSetPostWriter(long postId, GetClickedPostResponse postResponse) {
        if(!checkAnonymity(postId)) {
            postResponse.setUserInfo(new UserInfo("익명", null));
        } else {
            postResponse.setUserInfo(postInfoDao.getWriterInfo(postId));
        }
    }

    private boolean checkAnonymity(long postId) {
        return postInfoDao.isPostAnonymous(postId);
    }

    private boolean checkPostExists(long postId) {
        return postInfoDao.isPostExists(postId);
    }

    public GetScrolledCommentsResponse getValidatedCommentsByPostId(long postId, long userId, String orderBy) {
        log.info("PostService.getValidatedCommentsByPostId");
        GetScrolledCommentsResponse commentsResponse = new GetScrolledCommentsResponse();

        //댓글 수 설정(ok)
        Long countOfComments = postInfoDao.getCountOfComments(postId);
        commentsResponse.setCountOfComments(countOfComments);
        if(countOfComments == 0) {
            commentsResponse.setCommentList(null);
            return commentsResponse;
        }
        //부모 리스트 가져오기(ok)
        List<CommentInfo> parentComments = null;
        if(orderBy.equals("timeStandard")) {
            parentComments = postInfoDao.getTimeStandCommentsByPostId(postId);
        }
        if(orderBy.equals("likeStandard")) {
            parentComments = postInfoDao.getLikeStandCommentsByPostId(postId);
        }
        List<Long> parentCommentIds = parentComments.stream()
                .map(CommentInfo::getCommentId)
                .collect(Collectors.toList());
        Map<Long, Boolean> likesHistory = postInfoDao.getCommentLikesHistory(userId, parentCommentIds);
        Map<Long, Boolean> hatesHistory = postInfoDao.getCommentHatesHistory(userId, parentCommentIds);
        //부모 댓글 좋아요 실어요 여부 매핑(ok)
        for(CommentInfo parentComment : parentComments) {
            parentComment.setIsLiked(likesHistory.getOrDefault(parentComment.getCommentId(), false));
            parentComment.setIsHated(hatesHistory.getOrDefault(parentComment.getCommentId(), false));
        }
        //자식 댓글 가져오기
        List<CommentInfo> childComments = postInfoDao.getChildCommentsWithPrefer(userId, parentCommentIds);
        List<Long> childCommentIds = childComments.stream()
                .map(CommentInfo::getCommentId)
                .collect(Collectors.toList());
        //자식 댓글 좋아요 싫어요 여부 매핑
        Map<Long, Boolean> childLikeHistory = postInfoDao.getCommentLikesHistory(userId, childCommentIds);
        Map<Long, Boolean> childHateHistory = postInfoDao.getCommentHatesHistory(userId, childCommentIds);
        for(CommentInfo childComment : childComments) {
            childComment.setIsLiked(childLikeHistory.getOrDefault(childComment.getCommentId(), false));
            childComment.setIsHated(childHateHistory.getOrDefault(childComment.getCommentId(), false));
        }

        Map<Long, List<CommentInfo>> groupedChildComments = childComments.stream()
                .collect(Collectors.groupingBy(CommentInfo::getParentId));

        //댓글-대댓글 매핑하기
        List<FamilyComment> commentList = new ArrayList<>();
        for(CommentInfo parentComment : parentComments) {
            List<CommentInfo> childrenComment = groupedChildComments.getOrDefault(parentComment.getCommentId(), new ArrayList<>());
            commentList.add(new FamilyComment(parentComment, childrenComment));
        }
        commentsResponse.setCommentList(commentList);
        return commentsResponse;
    }

    public GetScrolledCommentsResponse getCommentsByPostId(long postId, String orderBy) {
        log.info("PostService.getCommentsByPostId");
        GetScrolledCommentsResponse commentsResponse = new GetScrolledCommentsResponse();

        //댓글 수 설정
        Long countOfComments = postInfoDao.getCountOfComments(postId);
        commentsResponse.setCountOfComments(countOfComments);
        if(countOfComments == 0) {
            commentsResponse.setCommentList(null);
            return commentsResponse;
        }

        //부모 리스트 가져오기
        List<CommentInfo> parentComments = null;
        if(orderBy.equals("timeStandard")) {
            parentComments = postInfoDao.getTimeStandCommentsByPostId(postId);
        }
        if(orderBy.equals("likeStandard")) {
            parentComments = postInfoDao.getLikeStandCommentsByPostId(postId);
        }
        List<Long> parentCommentIds = parentComments.stream()
                .map(CommentInfo::getCommentId)
                .collect(Collectors.toList());

        //자식 댓글 가져오기
        Map<Long, List<CommentInfo>> childComments = postInfoDao.getChildCommentsWithoutPrefer(parentCommentIds)
                .stream()
                .collect(Collectors.groupingBy(CommentInfo::getParentId));

        //댓글-대댓글 매핑하기
        List<FamilyComment> commentList = new ArrayList<>();
        for(CommentInfo parentComment : parentComments) {
            List<CommentInfo> childrenComment = childComments.getOrDefault(parentComment.getCommentId(), new ArrayList<>());
            commentList.add(new FamilyComment(parentComment, childrenComment));
        }
        commentsResponse.setCommentList(commentList);
        return commentsResponse;
    }
}
