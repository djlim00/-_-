package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.RecentKeywordDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecentKeywordService {
    private final RecentKeywordDao recentKeywordDao;


    public void deleteByUserId(long userId) {
        log.info("RecentKeywordService::deleteByUserId()");
        recentKeywordDao.deleteByUserId(userId);
    }
}
