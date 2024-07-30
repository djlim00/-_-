package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.bulletin.BulletinDto;
import com.kuit3.rematicserver.entity.Bulletin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<BulletinDto> findBulletinsByCategory(String category) {
        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource();

        if ("all".equalsIgnoreCase(category)) {
            sql = "SELECT *, " +
                    "CASE " +
                    "   WHEN LEFT(name, 1) >= '가' AND LEFT(name, 1) <= '깋' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '까' AND LEFT(name, 1) <= '낗' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '나' AND LEFT(name, 1) <= '닣' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '다' AND LEFT(name, 1) <= '딯' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '따' AND LEFT(name, 1) <= '띻' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '라' AND LEFT(name, 1) <= '맇' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '마' AND LEFT(name, 1) <= '밓' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '바' AND LEFT(name, 1) <= '빟' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '빠' AND LEFT(name, 1) <= '삫' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '사' AND LEFT(name, 1) <= '싷' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '싸' AND LEFT(name, 1) <= '앃' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '아' AND LEFT(name, 1) <= '잏' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '자' AND LEFT(name, 1) <= '짛' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '차' AND LEFT(name, 1) <= '칳' THEN 'ㅊ-ㅋ' " +
                    "   WHEN LEFT(name, 1) >= '카' AND LEFT(name, 1) <= '킿' THEN 'ㅊ-ㅋ' " +
                    "   WHEN LEFT(name, 1) >= '타' AND LEFT(name, 1) <= '팋' THEN 'ㅊ-ㅋ' " +
                    "   WHEN LEFT(name, 1) >= '파' AND LEFT(name, 1) <= '핗' THEN 'ㅍ-ㅎ' " +
                    "   WHEN LEFT(name, 1) >= '하' AND LEFT(name, 1) <= '힣' THEN 'ㅍ-ㅎ' " +
                    "   ELSE '기타' " +
                    "END AS nameGroup " +
                    "FROM Bulletin";
        } else {
            sql = "SELECT *, " +
                    "CASE " +
                    "   WHEN LEFT(name, 1) >= '가' AND LEFT(name, 1) <= '깋' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '까' AND LEFT(name, 1) <= '낗' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '나' AND LEFT(name, 1) <= '닣' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '다' AND LEFT(name, 1) <= '딯' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '따' AND LEFT(name, 1) <= '띻' THEN 'ㄱ-ㄷ' " +
                    "   WHEN LEFT(name, 1) >= '라' AND LEFT(name, 1) <= '맇' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '마' AND LEFT(name, 1) <= '밓' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '바' AND LEFT(name, 1) <= '빟' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '빠' AND LEFT(name, 1) <= '삫' THEN 'ㄹ-ㅂ' " +
                    "   WHEN LEFT(name, 1) >= '사' AND LEFT(name, 1) <= '싷' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '싸' AND LEFT(name, 1) <= '앃' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '아' AND LEFT(name, 1) <= '잏' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '자' AND LEFT(name, 1) <= '짛' THEN 'ㅅ-ㅈ' " +
                    "   WHEN LEFT(name, 1) >= '차' AND LEFT(name, 1) <= '칳' THEN 'ㅊ-ㅋ' " +
                    "   WHEN LEFT(name, 1) >= '카' AND LEFT(name, 1) <= '킿' THEN 'ㅊ-ㅋ' " +
                    "   WHEN LEFT(name, 1) >= '타' AND LEFT(name, 1) <= '팋' THEN 'ㅊ-ㅋ' " +
                    "   WHEN LEFT(name, 1) >= '파' AND LEFT(name, 1) <= '핗' THEN 'ㅍ-ㅎ' " +
                    "   WHEN LEFT(name, 1) >= '하' AND LEFT(name, 1) <= '힣' THEN 'ㅍ-ㅎ' " +
                    "   ELSE '기타' " +
                    "END AS nameGroup " +
                    "FROM Bulletin WHERE origin_category = :category";
            params.addValue("category", category);
        }

        return jdbcTemplate.query(sql, params, bulletinDtoRowMapper());
    }

    public RowMapper<Bulletin> bulletinRowMapper(){
        return (rs, rowNum) -> {
            Bulletin bulletin = Bulletin.builder()
                    .bulletinId(rs.getLong("bulletin_id"))
                    .name(rs.getString("name"))
                    .genre(rs.getString("genre"))
                    .category(rs.getString("category"))
                    .originCategory(rs.getString("origin_category"))
                    .thumbnailImageUrl(rs.getString("thumnail_image_url"))
//                    .PreviewVideoUrl(rs.getString("preview_video_url"))
                    .build();
            return bulletin;
        };
    }

    private RowMapper<BulletinDto> bulletinDtoRowMapper() {
        return (rs, rowNum) -> new BulletinDto(
                rs.getLong("bulletin_id"),
                rs.getString("name"),
                rs.getString("genre"),
                rs.getString("origin_category"),
                rs.getString("category"),
                rs.getString("thumnail_image_url"),
                rs.getString("status"),
                rs.getString("nameGroup")
        );
    }

}
