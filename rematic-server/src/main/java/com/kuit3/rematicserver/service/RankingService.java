package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.BulletinDao;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostImageDao;
import com.kuit3.rematicserver.dao.RankingDao;
import com.kuit3.rematicserver.dto.home.GetRankedPostDto;
import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Post;
import com.kuit3.rematicserver.entity.PostImage;
import com.kuit3.rematicserver.entity.Ranking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingDao rankingDao;
    private final PostDao postDao;
    private final BulletinDao bulletinDao;
    private final PostImageDao postImageDao;

//    public List<GetRankedPostDto> getRealtimeRanking() {
//        rankingDao.
//
//        return rankingDao.getRankingByCategory(null); // null로 전달하여 전체 랭킹을 가져옴
//    }
//
//    public List<GetRankedPostDto> getWebtoonRanking() {
//        return rankingDao.getRankingByCategory("webtoon");
//    }
//
//    public List<GetRankedPostDto> getWebnovelRanking() {
//        return rankingDao.getRankingByCategory("webnovel");
//    }
//
//    public List<GetRankedPostDto> getNovelRanking() {
//        return rankingDao.getRankingByCategory("novel");
//    }


    public List<GetRankedPostDto> getRankingByCategory(String category) {
        log.info("RankingService::getRankingByCategory()");

        List<Ranking> rankings;
        if (category.equals("all")) {
            rankings = rankingDao.findAllLimit(10L);
        } else {
            rankings = rankingDao.findByCategory(category);
        }

        List<GetRankedPostDto> dtos = new ArrayList<>();
        for(int i = 0; i < rankings.size(); i++) {
            Post post = postDao.findById(rankings.get(i).getPostId());
            Bulletin bulletin = bulletinDao.findById(post.getBulletinId());
            List<PostImage> postImages = postImageDao.getByPostId(post.getPostId());
            GetRankedPostDto dto = GetRankedPostDto.builder()
                    .rank(i + 1)
                    .post_id(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .bulletin(bulletin.getName())
                    .likes(post.getLikes())
                    .hates(post.getHates())
                    .views(post.getViews())
                    .scraps(post.getScraps())
                    .image_url(postImages.size() > 0 ? postImages.get(0).getImageUrl() : null)
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }


    public void deletePostFromRanking(Long postId) {
        log.info("RankingService::getRankingByCategory()");
        if(!rankingDao.existsByPostId(postId)){
            return;
        }
        log.info("delete and find ranking");
        rankingDao.deleteAll();
        List<String> categories = Arrays.asList("webtoon", "webnovel", "novel");
        for(String category : categories){
            List<Ranking> rankingList = postDao.findRankingByCategory(category);
            rankingList.forEach(ranking -> rankingDao.save(ranking));
        }
    }
}

