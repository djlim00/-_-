package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostHatesDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostHatesService {
    private final PostHatesDao postHatesDao;
    public void deleteHatesOfPost(Long postId) {
        log.info("PostHatesService::deleteByPostId()");
        postHatesDao.deleteByPostId(postId);
    }
}
