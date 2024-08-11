package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.home.GetRankedPostDto;
import com.kuit3.rematicserver.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("home")
@RequiredArgsConstructor
public class HomeController {
    private final RankingService rankingService;

    @GetMapping("ranking/realtime")
    public BaseResponse<List<GetRankedPostDto>> getRealtimeRanking() {
        log.info("HomeController::getRealtimeRanking()");
//        List<GetRankedPostDto> ranking = rankingService.getRealtimeRanking();

        List<GetRankedPostDto> ranking = rankingService.getRankingByCategory("all");
        return new BaseResponse<>(ranking);
    }

    @GetMapping("ranking/webtoon")
    public BaseResponse<List<GetRankedPostDto>> getWebtoonRanking() {
//        List<GetRankedPostDto> ranking = rankingService.getWebtoonRanking();

        List<GetRankedPostDto> ranking = rankingService.getRankingByCategory("webtoon");
        return new BaseResponse<>(ranking);
    }

    @GetMapping("ranking/webnovel")
    public BaseResponse<List<GetRankedPostDto>> getWebnovelRanking() {
//        List<GetRankedPostDto> ranking = rankingService.getWebnovelRanking();

        List<GetRankedPostDto> ranking = rankingService.getRankingByCategory("webnovel");
        return new BaseResponse<>(ranking);
    }

    @GetMapping("ranking/novel")
    public BaseResponse<List<GetRankedPostDto>> getNovelRanking() {
//        List<GetRankedPostDto> ranking = rankingService.getNovelRanking();

        List<GetRankedPostDto> ranking = rankingService.getRankingByCategory("novel");
        return new BaseResponse<>(ranking);
    }
}
