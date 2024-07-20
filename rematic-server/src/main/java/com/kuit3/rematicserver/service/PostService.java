package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.BulletinDao;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostImageDao;
import com.kuit3.rematicserver.dao.RecentKeywordDao;
import com.kuit3.rematicserver.dto.CreatePostResponse;
import com.kuit3.rematicserver.dto.CreatePostRequest;
import com.kuit3.rematicserver.dto.GetPostDto;
import com.kuit3.rematicserver.dto.GetSearchResultResponse;
import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDao;
    private final RecentKeywordDao recentKeywordDao;
    private final BulletinDao bulletinDao;
    private final PostImageDao postImageDao;
    private final S3Uploader s3Uploader;

    @Transactional
    public GetSearchResultResponse searchPageByKeywordAndCategory(Long userId, String keyword, String category, Long lastId) {
        log.info("PostService::getPageByKeywordAndCategory()");
        List<GetPostDto> page = postDao.getPage(keyword, category, lastId);
        boolean hasNext = checkNextPage(keyword, category, page);
        recentKeywordDao.saveKeyword(userId, keyword);
        return new GetSearchResultResponse(page, hasNext);
    }

    private boolean checkNextPage(String keyword, String category, List<GetPostDto> page) {
        boolean hasNext = false;
        if(page.size() > 0){
            Long nextStartingId = page.get(page.size() - 1).getPost_id();
            log.info("nextStartingId = " + nextStartingId);
            hasNext = postDao.hasNextPage(keyword, category, nextStartingId);
        }
        return hasNext;
    }

    public GetSearchResultResponse searchPageByKeywordAndCategory_guestmode(String keyword, String category, Long lastId) {
        log.info("PostService::getPageByKeywordAndCategory()");
        List<GetPostDto> page = postDao.getPage(keyword, category, lastId);
        boolean hasNext = checkNextPage(keyword, category, page);
        return new GetSearchResultResponse(page, hasNext);
    }

    public CreatePostResponse createPost(CreatePostRequest request) {
        log.info("PostService::createPost()");
        Bulletin bulletin = bulletinDao.findById(request.getBulletin_id());
        request.setCategory(bulletin.getCategory());
        return new CreatePostResponse(postDao.createPost(request));
    }

    public Long uploadImage(Long postId, MultipartFile image, String description) {
        log.info("PostService::uploadImage()");

        String fileUrl = s3Uploader.uploadFile(image);
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
}
