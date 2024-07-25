package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.bulletin.BulletinDto;
import com.kuit3.rematicserver.entity.Bulletin;

import java.util.List;

public interface BulletinDao {
    public Bulletin findById(Long bulletinId);
    public List<BulletinDto> findBulletinsByCategory(String category);
}
