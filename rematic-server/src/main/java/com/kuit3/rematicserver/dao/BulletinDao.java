package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.Bulletin;

public interface BulletinDao {
    public Bulletin findById(Long bulletinId);
}
