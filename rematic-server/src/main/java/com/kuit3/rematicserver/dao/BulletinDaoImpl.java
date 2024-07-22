package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.Bulletin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class BulletinDaoImpl implements BulletinDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BulletinDaoImpl(DataSource dataSource){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    @Override
    public Bulletin findById(Long bulletinId) {
        String sql = "select * from Bulletin where bulletin_id = :bulletin_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("bulletin_id", bulletinId);
        return jdbcTemplate.queryForObject(sql, param, bulletinRowMapper());
    }

    public RowMapper<Bulletin> bulletinRowMapper(){
        return (rs, rowNum) -> {
            Bulletin bulletin = Bulletin.builder()
                    .bulletinId(Long.parseLong(rs.getString("bulletin_id")))
                    .name(rs.getString("name"))
                    .genre(rs.getString("genre"))
                    .category(rs.getString("category"))
                    .originCategory(rs.getString("origin_category"))
                    .thumbnailImageUrl(rs.getString("thumnail_image_url"))
                    .PreviewVideoUrl(rs.getString("preview_video_url")).build();
            return bulletin;
        };
    }
}
