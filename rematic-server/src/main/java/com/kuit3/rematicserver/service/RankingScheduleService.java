package com.kuit3.rematicserver.service;

import com.kuit3.rematic_server.dao.RankingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class RankingScheduleService {

    private final RankingDao rankingDao;

    //매일 0분일 될 때마다 실행
    @Scheduled(cron = "0 0 * * * *")
    public void resetRealtimeViewsEveryDay() {
        log.info("RankingScheduleService.resetRealtimeViewsEveryDay");
        rankingDao.resetRealtimeViewsOnlyToday();
    }
}