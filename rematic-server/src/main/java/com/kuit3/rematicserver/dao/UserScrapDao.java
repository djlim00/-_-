package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.UserScrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Objects;

@Slf4j
@Repository
public class UserScrapDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserScrapDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int deleteByPostId(Long postId) {
        String sql = "DELETE FROM UserScrap WHERE post_id = :post_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.update(sql, param);
    }

    public long save(long userId, Long postId) {
        String sql = "insert into UserScrap(user_id, post_id) values(:userId, :postId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("postId", postId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean exists(long userId, Long postId) {
        String sql = "SELECT EXISTS(SELECT * FROM UserScrap WHERE user_id = :userId AND post_id = :postId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("postId", postId);
        return jdbcTemplate.queryForObject(sql, param, boolean.class);
    }

    public boolean existsByUserIdAndScrapId(long userId, Long scrapId) {
        String sql = "SELECT EXISTS(SELECT * FROM UserScrap WHERE user_id = :userId AND scrap_id = :scrapId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("scrapId", scrapId);
        return jdbcTemplate.queryForObject(sql, param, boolean.class);
    }

    public long deleteById(Long scrapId) {
        String sql = "DELETE FROM UserScrap WHERE scrap_id = :scrapId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("scrapId", scrapId);
        return jdbcTemplate.update(sql, param);
    }

    public UserScrap findById(Long scrapId) {
        String sql = "SELECT * FROM UserScrap WHERE scrap_id = :scrapId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("scrapId", scrapId);
        return jdbcTemplate.queryForObject(sql, param, userScrapRowMapper());
    }

    private RowMapper<UserScrap> userScrapRowMapper() {
        return (rs, rowNum) -> {
            UserScrap userScrap = new UserScrap(rs.getLong("scrap_id"), rs.getLong("user_id"), rs.getLong("post_id"));
            return userScrap;
        };
    }
}
