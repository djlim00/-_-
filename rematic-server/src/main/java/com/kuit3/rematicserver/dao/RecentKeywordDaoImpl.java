package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
        String sql = "INSERT INTO recent_keyword(keyword, user_id) VALUES(:keyword, :userId)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("keyword", keyword)
                .addValue("userId", userId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
