package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.RankingDao;
import com.kuit3.rematicserver.entity.Ranking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingSchedulingService {

    private final RankingDao rankingDao;
    private final PostDao postDao;

    //정각마다 실행
    @Scheduled(cron = "0 0 * * * *")
//    @Scheduled(fixedDelay = 60000)

    @Transactional
    public void saveRankingEveryHour() {
        log.info("RankingScheduleService::saveRankingEveryHour()");
        rankingDao.deleteAll();
        List<String> categories = Arrays.asList("webtoon", "webnovel", "novel");
        for(String category : categories){

            List<Ranking> rankingList = postDao.findRankingByCategory(category);
            rankingList.forEach(ranking -> rankingDao.save(ranking));
        }
    }
}