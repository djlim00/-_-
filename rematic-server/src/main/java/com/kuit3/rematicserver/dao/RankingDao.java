package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
@Slf4j
public class RankingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RankingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public int resetRealtimeViewsOnlyToday() {
        String sql = "update Post set realtime_views = :clearing where created_at >= now() - interval 12 hour;";
        Map<String, Object> param = Map.of("clearing", 0);
        return jdbcTemplate.update(sql, param);
    }
}
