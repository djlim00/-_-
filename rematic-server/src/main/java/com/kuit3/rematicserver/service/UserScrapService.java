package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.UserScrapDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScrapService {
    private final UserScrapDao userScrapDao;
    public void deleteScrapsOfPost(Long postId) {
        log.info("UserScrapService::deleteScrapsOfPost()");
        userScrapDao.deleteByPostId(postId);
    }

}
