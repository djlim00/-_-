package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.GetPostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

@Slf4j
@Repository
public class PostDaoImpl implements PostDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<GetPostDto> getPage(String keyword, String category, Long lastId) {
        String sql = "SELECT * FROM Post" +
                " WHERE (title LIKE :keyword OR content LIKE :keyword)" +
                " AND post_id > :lastId";
        if(!category.equals("all")){
            sql += " AND category = :category";
        }
        sql += " LIMIT 10";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("keyword",  "%" + keyword + "%")
                .addValue("lastId", lastId);
        if(!category.equals("all")){
            param.addValue("category", category);
        }

        return jdbcTemplate.query(sql, param, getPostDtoRowMapper);
    }

    @Override
    public boolean hasNextPage(String keyword, String category, Long lastId) {
        String sql = "SELECT EXISTS(SELECT * FROM Post " +
                "WHERE (title LIKE :keyword OR content LIKE :keyword)" +
                " AND post_id > :lastId";
        if(!category.equals("all")){
            sql += " AND category = :category";
        }
        sql += ")";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("keyword",  "%" + keyword + "%")
                .addValue("lastId", lastId);
        if(!category.equals("all")){
            param.addValue("category", category);
        }

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    private RowMapper<GetPostDto> getPostDtoRowMapper = (ResultSet rs, int rowNum)->{
        GetPostDto dto = GetPostDto.builder()
            .post_id(rs.getLong("post_id"))
            .title(rs.getString("title"))
            .content(rs.getString("content"))
                .likes(rs.getLong("likes"))
                .hates(rs.getLong("hates"))
                .views(rs.getLong("views"))
                .scraps(rs.getLong("scraps"))
                .image_url(rs.getString("images"))
            .build();
        return dto;
    };
}
