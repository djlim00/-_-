package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.domain.Work;
import com.kuit3.rematicserver.dto.search.UserRecentKeywordResponse;
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
        String sql = "select keyword from recent_keyword where user_id = :userId and status = 'active' order by created_at DESC limit 10";
        Map<String, Object> param = Map.of("userId", userId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            UserRecentKeywordResponse keywordResponse = new UserRecentKeywordResponse(
                    rs.getString("keyword")
            );
            return keywordResponse;
        });
    }

    public Integer modifyUserRecentKeyword(long userId, long keywordId) {
        String sql = "update Recent_Keyword set status = 'dormant' where user_id = :userId and recent_keyword_id = :keywordId;";
        Map<String, Object> param = Map.of(
                "userId", userId,
                "keywordId", keywordId);
        return jdbcTemplate.update(sql, param);
    }

    //TODO : 실시간 인기글의 사진이 있으면 그걸 가져오고, 없다면 인기글이 쓰인 작품의 대표 사진 가져오게 수정하기
    public String getTopPostPicUrl() {
        String sql = "select profile_image_url from User where user_id = :userId limit 1";
        Map<String, Object> param = Map.of("userId", 1);
        return jdbcTemplate.queryForObject(sql, param, String.class);
    }

    //TODO : 실시간 인기글의 제목과 카테고리를 불러오도록 수정하기
    public List<Work> getTopPostWorkInfo() {
        String sql = "select user_eamil, nickname from User where user_id = :userId limit 1";
        Map<String, Object> param = Map.of("userId", 1);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            Work work = new Work(
                    rs.getString("user_email"),
                    rs.getString("nickname")
            );
            return work;
        });
    }
}
