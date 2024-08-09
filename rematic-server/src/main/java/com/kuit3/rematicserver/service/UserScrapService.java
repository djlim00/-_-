package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.UserScrapDao;
import com.kuit3.rematicserver.entity.Post;
import com.kuit3.rematicserver.entity.UserScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScrapService {
    private final UserScrapDao userScrapDao;
    private final PostDao postDao;
    public void deleteScrapsOfPost(Long postId) {
        log.info("UserScrapService::deleteScrapsOfPost()");
        userScrapDao.deleteByPostId(postId);
    }

    public long create(long userId, Long postId) {
        log.info("UserScrapService::create()");
        Post post = postDao.findById(postId);
        postDao.incrementScraps(postId);
        return userScrapDao.save(userId, postId);
    }

    public boolean hasDuplicateScrap(long userId, Long postId) {
        log.info("UserScrapService::hasDuplicateScrap()");
        return userScrapDao.exists(userId, postId);
    }

    public boolean isScrapCreatedByUser(long userId, Long scrapId) {
        log.info("UserScrapService::existsByUserIdAndScrapId()");
        return userScrapDao.existsByUserIdAndScrapId(userId, scrapId);
    }

    public void deleteById(Long scrapId) {
        log.info("UserScrapService::deleteById()");
        UserScrap userScrap = userScrapDao.findById(scrapId);
        postDao.decrementScraps(userScrap.getPostId());
        userScrapDao.deleteById(scrapId);
    }

    public void deleteByUserId(long userId) {
        log.info("UserScrapService::deleteByUserId()");
        userScrapDao.deleteByUserId(userId);
    }
}
