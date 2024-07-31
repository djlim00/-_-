package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
import com.kuit3.rematicserver.dto.search.UserRecommendableKeywordsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SearchDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SearchDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    public List<UserRecentKeywordResponse> getKeywordsByUserId(long userId) {
        log.info("SearchDao.getKeywordsByUserId");
        String sql = "select keyword, recent_keyword_id from Recent_Keyword where user_id = :userId and " +
                "status = 'active' order by created_at DESC limit 10";
        Map<String, Object> param = Map.of("userId", userId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            UserRecentKeywordResponse keywordResponse = new UserRecentKeywordResponse(
                    rs.getString("keyword"),
                    rs.getLong("recent_keyword_id")
            );
            return keywordResponse;
        });
    }

    public Integer modifyUserRecentKeyword(long userId, long keywordId) {
        log.info("SearchDao.modifyUserRecentKeyword");
        String sql = "update Recent_Keyword set status = 'dormant' where user_id = :userId and recent_keyword_id = :keywordId;";
        Map<String, Object> param = Map.of(
                "userId", userId,
                "keywordId", keywordId);
        return jdbcTemplate.update(sql, param);
    }


    public Long hasUserRecentVisitedBulletin(long userId) {
        log.info("SearchDao.hasUserRecentVisitedBulletin");
        String sql = "select bulletin_id from User where user_id = :userId";
        Map<String, Object> param = Map.of("userId", userId);
        return jdbcTemplate.queryForObject(sql, param, Long.class);
    }

    public List<UserRecommendableKeywordsResponse> getFourRandomWorks() {
        log.info("SearchDao.getFourRandomWorks");
        String sql = "select name, category from Bulletin order by rand() limit :cnt";
        Map<String, Object> param = Map.of("cnt", 4);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            return new UserRecommendableKeywordsResponse(
                    rs.getString("name"),
                    rs.getString("category")
            );
        });
    }

    public List<UserRecommendableKeywordsResponse> getSameBulletinList(Long userRecentVisitedBulletin) {
        log.info("SearchDao.getSameBulletinList");
        String sql = "select b2.name, b2.category from Bulletin as b1 join Bulletin as b2 on b1.category = b2.category " +
                "where b1.bulletin_id = :bulletin_id and b2.bulletin_id <> :bulletin_id order by rand() limit 2;";
        Map<String, Object> param = Map.of("bulletin_id", userRecentVisitedBulletin);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            return new UserRecommendableKeywordsResponse(
                    rs.getString("name"),
                    rs.getString("category")
            );
        });
    }

    public List<UserRecommendableKeywordsResponse> getSameGenreList(Long userRecentVisitedBulletin) {
        log.info("SearchDao.getSameGenreList");
        String sql = "SELECT name, category FROM Bulletin WHERE genre = (SELECT genre FROM Bulletin " +
                "WHERE bulletin_id = :bulletin_id) AND bulletin_id <> :bulletin_id ORDER BY RAND() LIMIT 2;";
        Map<String, Object> param = Map.of("bulletin_id", userRecentVisitedBulletin);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            return new UserRecommendableKeywordsResponse(
                    rs.getString("name"),
                    rs.getString("category")
            );
        });
    }

    public boolean checkUserRecentKeyword(long userId, long keywordId) {
        String sql = "select exists (select 1 from Recent_keyword where user_id = :userId and recent_keyword_id = :keywordId);";
        Map<String, Object> param = Map.of("userId", userId, "keywordId", keywordId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }
}
