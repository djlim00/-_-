package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.BadRequestException;
import com.kuit3.rematicserver.common.exception.InvalidParameterException;
import com.kuit3.rematicserver.common.exception.PostNotFoundException;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostReportDao;
import com.kuit3.rematicserver.dto.post.PostReport;
import com.kuit3.rematicserver.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostReportService {
    @Autowired
    private final PostReportDao postReportDao;
    @Autowired
    private final PostDao postDao;
    @Autowired
    private final PunishmentService punishmentService;
    private final long REPORT_THRESHOLD = 10;
    @Transactional
    public void report(long userId, long postId, String type) {
        log.info("PostReportService::report()");

        Post reportedPost = null;
        try{
            reportedPost = postDao.findById(postId);
        }
        catch(EmptyResultDataAccessException e){
            throw new PostNotFoundException(POST_NOT_FOUND);
        }

        if(!(type.equals("abuse") || type.equals("obscene") || type.equals("unrelated") || type.equals("advertisement"))){
            throw new InvalidParameterException(INVALID_PARAM);
        }

        if(userId == reportedPost.getUserId()){
            throw new InvalidParameterException(INVALID_PARAM);
        }

        PostReport request = PostReport.builder()
                .reporterId(userId)
                .reportedUserId(reportedPost.getUserId())
                .postId(postId)
                .type(type)
                .build();
        postReportDao.create(request);

        if(postReportDao.countByPostIdAndType(postId, type) == REPORT_THRESHOLD){
            long reportedUserId = reportedPost.getUserId();
            long bulletinId = reportedPost.getBulletinId();

            // 유저에게 제한을 가하려면 이 메소드를 호출
            punishmentService.punishUser(reportedUserId, bulletinId, type);
        }
    }
}
