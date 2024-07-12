package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.RankingDao;
import com.kuit3.rematicserver.dto.GetRankedPostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingDao rankingDao;

    public List<GetRankedPostDto> getRealtimeRanking() {
        return rankingDao.getRankingByCategory(null); // null로 전달하여 전체 랭킹을 가져옴
    }

    public List<GetRankedPostDto> getWebtoonRanking() {
        return rankingDao.getRankingByCategory("웹툰");
    }

    public List<GetRankedPostDto> getWebnovelRanking() {
        return rankingDao.getRankingByCategory("웹소설");
    }

    public List<GetRankedPostDto> getNovelRanking() {
        return rankingDao.getRankingByCategory("소설");
    }
}