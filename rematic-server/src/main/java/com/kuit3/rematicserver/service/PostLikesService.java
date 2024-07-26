package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostLikesDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostLikesService {
    private final PostLikesDao postLikesDao;
    public void deleteLikesOfPost(Long postId) {
        log.info("PostLikesService::deleteByPostId()");
        postLikesDao.deleteByPostId(postId);
    }
}
