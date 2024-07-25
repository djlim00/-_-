package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.BulletinDao;
import com.kuit3.rematicserver.dto.bulletin.BulletinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BulletinService {
    private final BulletinDao bulletinDao;

    public List<BulletinDto> getBulletinsByCategory(String category) {
        return bulletinDao.findBulletinsByCategory(category);
    }
}
