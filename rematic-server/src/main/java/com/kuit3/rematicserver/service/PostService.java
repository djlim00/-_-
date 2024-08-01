package com.kuit3.rematicserver.service;


import com.kuit3.rematicserver.aws.S3Uploader;
import com.kuit3.rematicserver.common.exception.*;
import com.kuit3.rematicserver.dto.post.*;
import com.kuit3.rematicserver.dto.post.commentresponse.CommentInfo;
import com.kuit3.rematicserver.dto.post.commentresponse.FamilyComment;
import com.kuit3.rematicserver.dto.post.postresponse.PostInfo;
import com.kuit3.rematicserver.dao.BulletinDao;
import com.kuit3.rematicserver.dao.PostImageDao;
import com.kuit3.rematicserver.dto.post.CreatePostResponse;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;

import com.kuit3.rematicserver.common.exception.S3FileNumberLimitExceededException;

import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Post;

import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostInfoDao;
import com.kuit3.rematicserver.dao.RecentKeywordDao;

import com.kuit3.rematicserver.dto.search.SearchPostResponse;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;
import com.kuit3.rematicserver.entity.PostImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

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
    private final int MAX_IMAGE_NUMBER = 30;

    public SearchPostResponse getPage(String category, Long lastId){
        log.info("PostService::getPage()");
        return searchPage(null, "", category, lastId);
    }

    @Transactional
    public SearchPostResponse searchPage(Long userId, String keyword, String category, Long lastId) {
        log.info("PostService::searchPage()");
        List<SearchPostDto> page = postDao.getPage(keyword, category, lastId, 10L);
        boolean hasNext = checkNextPage(keyword, category, page);
        if(userId != null){
            recentKeywordDao.saveKeyword(userId, keyword);
        }
        return new SearchPostResponse(page, hasNext);
    }

    private boolean checkNextPage(String keyword, String category, List<SearchPostDto> page) {
        boolean hasNext = false;
        if(page.size() > 0){
            Long lastPostId = page.get(page.size() - 1).getPost_id();
            log.info("lastPostId = " + lastPostId);
            hasNext = postDao.hasNextPage(keyword, category, lastPostId);
        }
        return hasNext;
    }

    public CreatePostResponse createPost(CreatePostRequest request) {
        log.info("PostService::createPost()");
        Bulletin bulletin = bulletinDao.findById(request.getBulletin_id());
        request.setCategory(bulletin.getOriginCategory());
        request.setGenre(bulletin.getGenre());
        return new CreatePostResponse(postDao.createPost(request));
    }

    @Transactional
    public Long uploadImage(Long postId, MultipartFile image, String description) {
        log.info("PostService::uploadImage()");

        String fileUrl = s3Uploader.uploadFile(image);
//        String fileUrl = "test.png";

        // 이미지 순서를 별도의 칼럼에 저장하는 경우 사용
//        Long currentOrder = 0L;
//        List<PostImage> postImages = postImageDao.getByPostId(postId);
//        if(postImages != null){
//            for(PostImage postImage : postImages){
//                if(currentOrder > postImage.getPostImageId()){
//                    currentOrder = postImage.getPostImageId();
//                }
//            }
//        }
//        currentOrder += 1;

        if(postImageDao.getByPostId(postId).size() >= MAX_IMAGE_NUMBER){
            throw new S3FileNumberLimitExceededException(FILE_LIMIT_EXCEEDED);
        }

        if(description != null){
            return postImageDao.save(postId, fileUrl, description);
        }
        return postImageDao.savePostImageWithoutDescription(postId, fileUrl);
    }

    public boolean checkPostWriter(long userId, Long postId) {
        log.info("PostService::checkPostWriter()");

        Post post = postDao.findById(postId);
        return post.getUserId() == userId;
    }

    public boolean existsById(Long postId) {
        return postDao.existsById(postId);
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
        //사용자가 차단한 사용자ID 목록 가져오기
        List<Long> deniedUserIds = postInfoDao.getDeniedUsers(userId);
        parentComments.removeIf(parentComment -> deniedUserIds.contains(parentComment.getWriterId()));

        List<Long> parentCommentIds = parentComments.stream()
                .map(CommentInfo::getCommentId)
                .collect(Collectors.toList());
        Map<Long, Boolean> likesHistory = postInfoDao.getCommentLikesHistory(userId, parentCommentIds);
        Map<Long, Boolean> hatesHistory = postInfoDao.getCommentHatesHistory(userId, parentCommentIds);
        //부모 댓글 좋아요 실어요 여부 매핑
        for(CommentInfo parentComment : parentComments) {
            parentComment.setIsLiked(likesHistory.getOrDefault(parentComment.getCommentId(), false));
            parentComment.setIsHated(hatesHistory.getOrDefault(parentComment.getCommentId(), false));
        }
        //자식 댓글 가져오기
        List<CommentInfo> childComments = postInfoDao.getChildCommentsWithPrefer(userId, parentCommentIds);
        childComments.removeIf(childComment -> deniedUserIds.contains(childComment.getWriterId()));
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

    public PostCommentResponse leaveNewComment(long userId, long postId, PostCommentRequest request) {
        log.info("PostService.leaveNewComment");
        //댓글 작성 중에 게시글이 삭제되었는 지 확인하는 것.
        if(!checkPostExists(postId)) {
            throw new PostNotFoundException(POST_NOT_FOUND);
        }
        log.info("pass post Exists Checking");
        //대댓글일 때 부모 댓글이 삭제되었는지 확인하는 것.
        if(request.getParentCommentId() != 0 && !checkParentCommentExists(request.getParentCommentId())) {
            throw new CommentNotFoundException(PARENT_COMMENT_NOT_EXISTS);
        }
        log.info("pass ParentComment Exists checking");
        List<Long> result = postInfoDao.leaveCommentWrittenByUser(userId, postId, request);
        //댓글 등록이 잘 됐는지 확인
        if(result.get(1) != 1) {
            throw new UserCommentException(WRONG_COMMENT_REGISTER);
        }
        return new PostCommentResponse(result.get(0));
    }

    private boolean checkParentCommentExists(Long parentCommentId) {
        return postInfoDao.chekcParentCommentExists(parentCommentId);
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

    public String uploadCommentImage(long userId, long commentId, MultipartFile image) {
        log.info("PostService.uploadCommentImage");
        String fileUrl = s3Uploader.uploadFile(image);
        if(checkImageUrlExists(commentId)) {
            throw new CommentImageDuplicateException(IMAGE_ALREADY_EXISTS);
        }
        int result = postInfoDao.saveUrlFromS3(fileUrl, commentId, userId);
        if(result != 1) {
            return "failed to saving comment image";
        } else {
            return "complete saving comment image";
        }
    }

    private boolean checkImageUrlExists(long commentId) {
        return postImageDao.hasImageUrlAlready(commentId);
    }

    public boolean isUserMatchesComment(long userId, long commentId) {
        return postInfoDao.checkUserCommentMatch(userId, commentId);
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

    @Transactional
    public void modifyPost(Long postId, PatchPostDto dto) {
        log.info("PostService::modifyPost()");
        postDao.update(postId, dto.getTitle(), dto.getContent());
        modifyPostImages(postId, dto.getImages());
    }

    public void modifyPostImages(Long postId, List<PostImageDto> modifiedImages) {
        log.info("PostService::modifyPostImages()");

        postImageDao.modifyStatusDormantByPostId(postId);
        for(PostImageDto dto : modifiedImages){
            PostImage savedPostImage = postImageDao.getById(dto.getPost_image_id());
            postImageDao.deleteById(dto.getPost_image_id());
            postImageDao.save(postId, savedPostImage.getImageUrl(), dto.getImage_description());
        }
    }

    @Transactional
    public SearchPostResponse searchBulletinPage(Long userId, Long bulletinId, String keyword, Long lastId) {
        log.info("PostService::searchBulletinPage()");
        List<SearchPostDto> page = postDao.getBulletinPosts(bulletinId, keyword, lastId, 10L);
        boolean hasNext = checkNextBulletinPage(bulletinId, keyword, page);
        recentKeywordDao.saveKeyword(userId, keyword);
        return new SearchPostResponse(page, hasNext);
    }

    public SearchPostResponse searchBulletinPage_guestmode(Long bulletinId, String keyword, Long lastId) {
        log.info("PostService::searchBulletinPage_guestmode()");
        List<SearchPostDto> page = postDao.getBulletinPosts(bulletinId, keyword, lastId, 10L);
        boolean hasNext = checkNextBulletinPage(bulletinId, keyword, page);
        return new SearchPostResponse(page, hasNext);
    }

    private boolean checkNextBulletinPage(Long bulletinId, String keyword, List<SearchPostDto> page) {
        log.info("PostService::searchBulletinPage()");

        boolean hasNext = false;
        if(page.size() > 0){
            Long lastPostId = page.get(page.size() - 1).getPost_id();
            log.info("lastPostId = " + lastPostId);
            hasNext = postDao.hasNextBulletinPage(bulletinId, keyword, lastPostId);
        }
        return hasNext;
    }
}
