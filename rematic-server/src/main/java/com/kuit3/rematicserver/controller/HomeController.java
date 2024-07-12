package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.GetRankedPostDto;
import com.kuit3.rematicserver.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        List<GetRankedPostDto> ranking = rankingService.getRealtimeRanking();
        return new BaseResponse<>(ranking);
    }

    @GetMapping("ranking/webtoon")
    public BaseResponse<List<GetRankedPostDto>> getWebtoonRanking() {
        List<GetRankedPostDto> ranking = rankingService.getWebtoonRanking();
        return new BaseResponse<>(ranking);
    }

    @GetMapping("ranking/webnovel")
    public BaseResponse<List<GetRankedPostDto>> getWebnovelRanking() {
        List<GetRankedPostDto> ranking = rankingService.getWebnovelRanking();
        return new BaseResponse<>(ranking);
    }

    @GetMapping("ranking/novel")
    public BaseResponse<List<GetRankedPostDto>> getNovelRanking() {
        List<GetRankedPostDto> ranking = rankingService.getNovelRanking();
        return new BaseResponse<>(ranking);
    }
}
