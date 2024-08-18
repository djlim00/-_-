package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.BulletinDao;
import com.kuit3.rematicserver.dao.PunishmentDao;
import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Punishment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PunishmentService {
    private final PunishmentDao punishmentDao;
    private final BulletinDao bulletinDao;

    @Transactional
    /*
        @Param
        reportedUserId : 신고 당한 유저 id
        bulletinId : 신고당한 유저가 활동한 게시판 id
        type : 신고 유형 (컨트롤러에서 받는 type과 동일)
     */
    public void punishUser(long reportedUserId, long bulletinId, String type) {
        log.info("PunishmentService::punishUser()");

        List<Punishment> list = punishmentDao.findByUserId(reportedUserId);
        long punishmentCount = list.size();

        LocalDateTime endAt;
        log.info("list.size() = " + list.size());
        if(list.size() > 0){
            endAt = list.get(list.size() - 1).getEndAt();
        }
        else{
            endAt = LocalDateTime.now();
        }

        String content = "경고";
        if(punishmentCount + 1 == 6){
            endAt = endAt.plusDays(14);
            content = "이용정지 14일";
        }
        else if(punishmentCount + 1 == 4){
            endAt = endAt.plusDays(7);
            content = "이용정지 7일";
        }
        else if(punishmentCount + 1 == 3){
            endAt = endAt.plusDays(3);
            content = "이용정지 3일";
        }
        else if(punishmentCount + 1 == 2){
            endAt = endAt.plusDays(1);
            content = "이용정지 1일";
        }

        String reason;
        Bulletin bulletin = bulletinDao.findById(bulletinId);
        reason = bulletin.getName() + " 게시판 (";
        if(type.equals("abuse")){
            reason += "욕설/비하";
        }
        else if(type.equals("obscene")){
            reason += "음란물/불건전 내용";
        }
        else if(type.equals("unrelated")){
            reason += "게시판 성격 부적절";
        }
        else if(type.equals("advertisement")){
            reason += "상업성 광고 및 판매";
        }
        reason += ")";

        Punishment punishment = Punishment
                .builder()
                .reason(reason)
                .content(content)
                .endAt(endAt)
                .userId(reportedUserId)
                .build();

        punishmentDao.create(punishment);
    }
}
