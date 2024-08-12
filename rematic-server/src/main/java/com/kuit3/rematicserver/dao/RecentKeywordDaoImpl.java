package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class RecentKeywordDaoImpl implements RecentKeywordDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RecentKeywordDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long saveKeyword(Long userId, String keyword) {
        String sql = "INSERT INTO Recent_Keyword(keyword, user_id) VALUES(:keyword, :userId)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("keyword", keyword)
                .addValue("userId", userId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public int deleteByUserId(long userId) {
        String sql = "DELETE FROM Recent_Keyword WHERE user_id = :userId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId);
        return jdbcTemplate.update(sql, param);
    }

    @Override
    public List<Long> findByUserId(long userId) {
        String sql = "SELECT recent_keyword_id FROM Recent_Keyword WHERE user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> rs.getLong("recent_keyword_id"));
    }
}
