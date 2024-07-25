package com.kuit3.rematicserver.service;


import com.kuit3.rematicserver.aws.S3Uploader;
import com.kuit3.rematicserver.dao.BulletinDao;
import com.kuit3.rematicserver.dao.PostImageDao;
import com.kuit3.rematicserver.dto.CreatePostResponse;
import com.kuit3.rematicserver.dto.CreatePostRequest;
import com.kuit3.rematicserver.dto.GetPostUpdateFormDto;
import com.kuit3.rematicserver.dto.PostImageDto;
import com.kuit3.rematicserver.dto.post.postresponse.PostInfo;
import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Post;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostInfoDao;
import com.kuit3.rematicserver.dao.RecentKeywordDao;
import com.kuit3.rematicserver.dto.post.GetSearchPostDto;
import com.kuit3.rematicserver.dto.post.GetScrolledCommentsResponse;
import com.kuit3.rematicserver.dto.post.commentresponse.CommentInfo;
import com.kuit3.rematicserver.dto.post.commentresponse.FamilyComment;
import com.kuit3.rematicserver.dto.search.GetSearchPostResponse;
import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;

import com.kuit3.rematicserver.entity.PostImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;
    private final RecentKeywordDao recentKeywordDao;
    private final BulletinDao bulletinDao;
    private final PostImageDao postImageDao;
    private final S3Uploader s3Uploader;
    private final PostInfoDao postInfoDao;

    public GetSearchPostResponse getPage(String category, Long lastId){
        log.info("PostService::getPage()");
        return searchPage(null, "", category, lastId);
    }

    @Transactional
    public GetSearchPostResponse searchPage(Long userId, String keyword, String category, Long lastId) {
        log.info("PostService::searchPage()");
        List<GetSearchPostDto> page = postDao.getPage(keyword, category, lastId, 10L);
        boolean hasNext = checkNextPage(keyword, category, page);
        if(userId != null){
            recentKeywordDao.saveKeyword(userId, keyword);
        }
        return new GetSearchPostResponse(page, hasNext);
    }

    private boolean checkNextPage(String keyword, String category, List<GetSearchPostDto> page) {
        boolean hasNext = false;
        if(page.size() > 0){
            Long nextStartingId = page.get(page.size() - 1).getPost_id();
            log.info("nextStartingId = " + nextStartingId);
            hasNext = postDao.hasNextPage(keyword, category, nextStartingId);
        }
        return hasNext;
    }

    public CreatePostResponse createPost(CreatePostRequest request) {
        log.info("PostService::createPost()");
        Bulletin bulletin = bulletinDao.findById(request.getBulletin_id());
        request.setCategory(bulletin.getCategory());
        return new CreatePostResponse(postDao.createPost(request));
    }

    public Long uploadImage(Long postId, MultipartFile image, String description) {
        log.info("PostService::uploadImage()");

        //String fileUrl = s3Uploader.uploadFile(image);
        String fileUrl = "test.png";
        if(description != null){
            return postImageDao.savePostImage(postId, fileUrl, description);
        }
        return postImageDao.savePostImageWithoutDescription(postId, fileUrl);
    }

    public boolean checkPostWriter(long userId, Long postId) {
        log.info("PostService::checkPostWriter()");

        Post post = postDao.findById(postId);
        return post.getUserId() == userId;
    }

    public boolean hasPostWithId(Long postId) {
        return postDao.hasPostWithId(postId);
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

    public GetScrolledCommentsResponse getCommentsByPostId(long postId, long lastId, String orderBy) {
        log.info("PostService.getCommentsByPostId");
        //댓글 수 설정
        Long countOfComments = postInfoDao.getCountOfComments(postId);
        //부모 리스트 가져오기
        List<CommentInfo> parentComments = null;
        if(orderBy.equals("timeStandard")) {
            parentComments = postInfoDao.getTimeStandCommentsByPostId(postId, lastId);
        }
        if(orderBy.equals("likeStandard")) {
            parentComments = postInfoDao.getLikeStandCommentsByPostId(postId, lastId);
        }
        List<Long> parentCommentIds = parentComments.stream()
                .map(CommentInfo::getCommentId)
                .collect(Collectors.toList());
        //자식 댓글 가져오기
        Map<Long, List<CommentInfo>> childComments = postInfoDao.getChildCommentsTimeStand(parentCommentIds)
                .stream()
                .collect(Collectors.groupingBy(CommentInfo::getParentId));
        //댓글-대댓글 매핑하기
        List<FamilyComment> commentList = new ArrayList<>();
        for(CommentInfo parentComment : parentComments) {
            List<CommentInfo> childrenComment = childComments.getOrDefault(parentComment.getCommentId(), new ArrayList<>());
            commentList.add(new FamilyComment(parentComment, childrenComment));
        }
        GetScrolledCommentsResponse commentsResponse = new GetScrolledCommentsResponse();
        commentsResponse.setCommentList(commentList);
        commentsResponse.setCountOfComments(countOfComments);
        commentsResponse.setLastId(parentComments.isEmpty() ? lastId : parentComments.get(parentComments.size() - 1).getCommentId());
        return commentsResponse;
    }

    public void deletePost(Long postId) {
        log.info("PostService::deletePost()");

        postDao.modifyStatusDormant(postId);
        postImageDao.modifyStatusDormantByPostId(postId);
    }

    public GetPostUpdateFormDto getPostUpdateForm(Long postId) {
        log.info("PostService::getPostUpdateForm()");
        PostInfo postInfo = postInfoDao.getPostInfo(postId);
        List<PostImage> postImages = postImageDao.getByPostId(postId);

        GetPostUpdateFormDto dto = GetPostUpdateFormDto.builder()
                .title(postInfo.getTitle())
                .content(postInfo.getContent())
                .images(postImages.stream().map(PostImageDto::entityToDto).collect(Collectors.toList())).build();
        return dto;
    }
}
