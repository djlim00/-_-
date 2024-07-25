package com.kuit3.rematicserver.controller;

import com.kuit3.rematicserver.common.response.BaseResponse;
import com.kuit3.rematicserver.dto.bulletin.BulletinDto;
import com.kuit3.rematicserver.service.BulletinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("bulletin")
@RequiredArgsConstructor
public class BulletinController {
    private final BulletinService bulletinService;

    @GetMapping
    public BaseResponse<List<BulletinDto>> getBulletinsByCategory(@RequestParam String category) {
        List<BulletinDto> bulletins = bulletinService.getBulletinsByCategory(category);
        return new BaseResponse<>(bulletins);
    }
}
