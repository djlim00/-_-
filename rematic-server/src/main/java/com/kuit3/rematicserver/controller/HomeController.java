package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.GetPostDto;
import com.kuit3.rematicserver.dto.GetRankedPostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("home")
public class HomeController {
    @RequestMapping("ranking/realtime")
    public BaseResponse<List<GetRankedPostDto>> getRealtimeRanking(){

        return new BaseResponse<>(null);
    }
}
